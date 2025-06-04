package ru.evgenykuzakov.domain.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.evgenykuzakov.common.Resource
import ru.evgenykuzakov.domain.model.AgeGroups
import ru.evgenykuzakov.domain.model.AgeSexStatistic
import ru.evgenykuzakov.domain.model.Sex
import ru.evgenykuzakov.domain.model.Statistic
import ru.evgenykuzakov.domain.model.StatisticUIState
import ru.evgenykuzakov.domain.model.User
import ru.evgenykuzakov.domain.model.VisitorType
import ru.evgenykuzakov.domain.model.VisitorsByDate
import ru.evgenykuzakov.domain.use_case.GetStatisticsUseCase
import ru.evgenykuzakov.domain.use_case.GetUsersUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticInteractor(
    private val getUsersUseCase: GetUsersUseCase,
    private val getStatisticsUseCase: GetStatisticsUseCase
) {
    operator fun invoke(): Flow<Resource<StatisticUIState>> =
        getUsersUseCase().flatMapLatest { usersRes ->
            when (usersRes) {
                is Resource.Loading -> flowOf(Resource.Loading())
                is Resource.Error -> flowOf(Resource.Error(usersRes.message))
                is Resource.Success -> {

                    getStatisticsUseCase().map { statRes ->
                        when (statRes) {
                            is Resource.Loading -> Resource.Loading()
                            is Resource.Error -> Resource.Error(statRes.message)
                            is Resource.Success -> {
                                val users = usersRes.data
                                val stats = statRes.data
                                withContext(Dispatchers.Default) {
                                    coroutineScope {
                                        val deferredVisitorsByData = async {
                                            calcVisitorsByDate(stats = stats)
                                        }
                                        val deferredVisitors = async {
                                            calcVisitorsByType(
                                                stats = stats,
                                                users = users,
                                                type = VisitorType.VIEW
                                            )
                                        }
                                        val deferredSubscribers = async {
                                            calcVisitorsByType(
                                                stats = stats,
                                                users = users,
                                                type = VisitorType.SUBSCRIPTION
                                            )
                                        }
                                        val deferredUnsubscribers = async {
                                            calcVisitorsByType(
                                                stats = stats,
                                                users = users,
                                                type = VisitorType.UNSUBSCRIPTION
                                            )
                                        }
                                        val deferredMostOftenVisitors = async {
                                            calcMostOftenVisitors(stats = stats, users = users)
                                        }
                                        val deferredSexAgeStat = async {
                                            calcSexAgeStat(stats = stats, users = users)
                                        }
                                        Resource.Success(
                                            StatisticUIState(
                                                visitorsByDate = deferredVisitorsByData.await(),
                                                visitors = deferredVisitors.await(),
                                                subscribers = deferredSubscribers.await(),
                                                unsubscribers = deferredUnsubscribers.await(),
                                                mostOftenVisitors = deferredMostOftenVisitors.await(),
                                                ageSexStat = deferredSexAgeStat.await(),
                                            )
                                        )
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }


    private fun calcMostOftenVisitors(
        users: List<User>,
        stats: List<Statistic>
    ): List<User> = users.associateWith { user ->
        stats
            .filter { it.userId == user.id && it.type == VisitorType.VIEW }
            .sumOf { it.dates.size }
    }
        .entries
        .sortedByDescending { it.value }
        .take(3)
        .map { it.key }


    private fun calcVisitorsByDate(
        stats: List<Statistic>
    ): List<VisitorsByDate> {
        val usersPerDay = mutableMapOf<Int, MutableSet<Int>>()
        stats
            .filter { it.type == VisitorType.VIEW }
            .forEach { stat ->
                stat.dates.forEach { date ->
                    val userSet = usersPerDay.getOrPut(date) { mutableSetOf() }
                    userSet.add(stat.userId)
                }
            }

        return usersPerDay.mapValues { it.value.size }.map { VisitorsByDate(it.key, it.value) }
    }

    private fun calcVisitorsByType(
        type: VisitorType,
        users: List<User>,
        stats: List<Statistic>
    ): Int = users.associateWith { user ->
        stats
            .filter { it.userId == user.id && it.type == type }
            .sumOf { it.dates.size }
    }
        .values
        .sumOf { it }

    private fun calcSexAgeStat(
        users: List<User>,
        stats: List<Statistic>
    ): List<AgeSexStatistic> {
        val usersById = users.associateBy { it.id }
        val counter = mutableMapOf<Pair<AgeGroups, Sex>, Int>()
        stats
            .filter { it.type == VisitorType.VIEW }
            .forEach { stat ->
                val user = usersById[stat.userId]
                if (user != null) {
                    val ageGroup = AgeGroups.entries.firstOrNull { user.age in it.range }
                        ?: AgeGroups.GROUP_50plus
                    val key = ageGroup to user.sex
                    val currentCount = counter.getOrDefault(key, 0)
                    counter[key] = currentCount + stat.dates.size
                }
            }
        return counter.map { (key, count) ->
            AgeSexStatistic(
                ageGroup = key.first,
                sex = key.second,
                visitorsCount = count
            )
        }
    }

}