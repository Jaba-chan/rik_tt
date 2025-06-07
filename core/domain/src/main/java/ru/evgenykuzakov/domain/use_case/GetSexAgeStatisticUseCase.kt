package ru.evgenykuzakov.domain.use_case

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.evgenykuzakov.domain.model.AgeGroups
import ru.evgenykuzakov.domain.model.AgeSexStatistic
import ru.evgenykuzakov.domain.model.ByAgeSexStatisticFilter
import ru.evgenykuzakov.domain.model.Sex
import ru.evgenykuzakov.domain.model.Statistic
import ru.evgenykuzakov.domain.model.User
import ru.evgenykuzakov.domain.model.VisitorType
import java.time.LocalDate

class GetSexAgeStatisticUseCase {
    operator fun invoke(
        nowDate: LocalDate,
        filter: ByAgeSexStatisticFilter,
        users: List<User>,
        stats: List<Statistic>
    ): Flow<List<AgeSexStatistic>> = flow {
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
        emit(
            counter.map { (key, count) ->
                AgeSexStatistic(
                    ageGroup = key.first,
                    sex = key.second,
                    visitorsCount = count
                )
            }
        )
    }.flowOn(Dispatchers.Default)
}