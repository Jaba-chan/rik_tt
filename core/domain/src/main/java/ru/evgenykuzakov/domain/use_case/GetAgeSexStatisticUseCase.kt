package ru.evgenykuzakov.domain.use_case

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.evgenykuzakov.domain.model.AgeGroups
import ru.evgenykuzakov.domain.model.AgeSexStat
import ru.evgenykuzakov.domain.model.AgeSexStatisticResult
import ru.evgenykuzakov.domain.model.ByAgeSexStatisticFilter
import ru.evgenykuzakov.domain.model.Sex
import ru.evgenykuzakov.domain.model.Statistic
import ru.evgenykuzakov.domain.model.User
import ru.evgenykuzakov.domain.model.VisitorType
import java.time.LocalDate

class GetAgeSexStatisticUseCase {
    operator fun invoke(
        nowDate: LocalDate,
        filter: ByAgeSexStatisticFilter,
        users: List<User>,
        stats: List<Statistic>
    ): Flow<AgeSexStatisticResult> = flow {
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

                    val count = stat.dates.count { date ->
                        when (filter) {
                            ByAgeSexStatisticFilter.DAY -> date.isEqual(nowDate)
                            ByAgeSexStatisticFilter.WEEK -> date.isAfter(nowDate.minusDays(7)) || date.isEqual(nowDate)
                            ByAgeSexStatisticFilter.MONTH -> date.isAfter(nowDate.minusMonths(1)) || date.isEqual(nowDate)
                            ByAgeSexStatisticFilter.ALL -> true
                        }
                    }
                    if (count > 0) {
                        val currentCount = counter.getOrDefault(key, 0)
                        counter[key] = currentCount + count
                    }
                }
            }

        val statsList = counter
            .toList()
            .sortedWith(compareBy(
                { it.first.first.ordinal },
                { it.first.second.ordinal }
            ))
            .map { (key, count) ->
                AgeSexStat(
                    ageGroup = key.first,
                    sex = key.second,
                    visitorsCount = count
                )
            }

        val menCount = statsList.filter { it.sex == Sex.MAN }.sumOf { it.visitorsCount }
        val womenCount = statsList.filter { it.sex == Sex.WOMAN }.sumOf { it.visitorsCount }

        emit(
            AgeSexStatisticResult(
                stats = statsList,
                menCount = menCount,
                womenCount = womenCount)
        )
    }.flowOn(Dispatchers.Default)
}