package ru.evgenykuzakov.domain.use_case

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.evgenykuzakov.domain.model.ByDateStatisticFilter
import ru.evgenykuzakov.domain.model.Statistic
import ru.evgenykuzakov.domain.model.VisitorType
import ru.evgenykuzakov.domain.model.VisitorsByDate
import java.time.LocalDate

class GetVisitorsByDateUseCase {
    operator fun invoke(
        nowDate: LocalDate,
        filter: ByDateStatisticFilter,
        stats: List<Statistic>
    ): Flow<List<VisitorsByDate>> = flow {
        val visitsPerDay = mutableMapOf<LocalDate, Int>()
        stats
            .filter { it.type == VisitorType.VIEW }
            .forEach { stat ->
                stat.dates.forEach { date ->
                    visitsPerDay[date] = visitsPerDay.getOrDefault(date, 0) + 1
                }
            }

        val allDates = visitsPerDay.keys
        if (allDates.isNotEmpty()) {
            val minDate = allDates.min()
            val maxDate = allDates.max()
            val completeDateRange = generateSequence(minDate) { it.plusDays(1) }
                .takeWhile { !it.isAfter(maxDate) }
                .toList()
            val result = completeDateRange.map { date ->
                VisitorsByDate(
                    date = date,
                    visitors = visitsPerDay[date] ?: 0
                )
            }

            emit(result)
        } else {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.Default)
}