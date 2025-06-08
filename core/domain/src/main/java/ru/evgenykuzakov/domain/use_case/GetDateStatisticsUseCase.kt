package ru.evgenykuzakov.domain.use_case

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.evgenykuzakov.domain.model.ByDateStatisticFilter
import ru.evgenykuzakov.domain.model.DateStatistic
import ru.evgenykuzakov.domain.model.Statistic
import java.time.LocalDate
import kotlin.math.abs

class GetDateStatisticsUseCase(
    private val getVisitsPerDayUseCase: GetVisitsPerDayUseCase
) {
    operator fun invoke(
        nowDate: LocalDate,
        filter: ByDateStatisticFilter,
        stats: List<Statistic>
    ): Flow<List<DateStatistic>> = flow {
        val visitsPerDay = getVisitsPerDayUseCase(stats)

        if (visitsPerDay.isEmpty()) {
            emit(emptyList())
            return@flow
        }

        val result = when (filter) {
            ByDateStatisticFilter.DAY -> prepareDayStatistics(visitsPerDay)
            ByDateStatisticFilter.WEEK -> prepareWeekStatistics(visitsPerDay, nowDate)
            ByDateStatisticFilter.MONTH -> prepareMonthStatistics(visitsPerDay, nowDate)
        }

        emit(result)
    }.flowOn(Dispatchers.Default)

    private fun prepareDayStatistics(visitsPerDay: Map<LocalDate, Int>): List<DateStatistic> {
        val allDates = visitsPerDay.keys
        val minDate = allDates.min()
        val maxDate = allDates.max()

        val completeDateRange = generateSequence(minDate) { it.plusDays(1) }
            .takeWhile { !it.isAfter(maxDate) }
            .toList()

        return completeDateRange.map { date ->
            DateStatistic(
                date = date,
                visitors = visitsPerDay[date] ?: 0
            )
        }
    }

    private fun prepareWeekStatistics(visitsPerDay: Map<LocalDate, Int>, nowDate: LocalDate): List<DateStatistic> {
        val daysSinceNow = { date: LocalDate ->
            abs(nowDate.toEpochDay() - date.toEpochDay()).toInt()
        }

        return visitsPerDay.entries
            .groupBy { entry ->
                daysSinceNow(entry.key) / 7
            }
            .map { (weekIndex, entries) ->
                val firstDayOfGroup = nowDate.minusDays(weekIndex * 7L)
                DateStatistic(
                    date = firstDayOfGroup,
                    visitors = entries.sumOf { it.value }
                )
            }
            .sortedBy { it.date }
    }

    private fun prepareMonthStatistics(visitsPerDay: Map<LocalDate, Int>, nowDate: LocalDate): List<DateStatistic> {
        val daysSinceNow = { date: LocalDate ->
            abs(nowDate.toEpochDay() - date.toEpochDay()).toInt()
        }

        return visitsPerDay.entries
            .groupBy { entry ->
                daysSinceNow(entry.key) / 30
            }
            .map { (monthIndex, entries) ->
                val targetMonth = nowDate.minusMonths(monthIndex.toLong())
                val firstDayOfGroup = targetMonth.withDayOfMonth(
                    nowDate.dayOfMonth.coerceAtMost(targetMonth.lengthOfMonth())
                )
                DateStatistic(
                    date = firstDayOfGroup,
                    visitors = entries.sumOf { it.value }
                )
            }
            .sortedBy { it.date }
    }
}
