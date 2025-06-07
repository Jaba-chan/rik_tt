package ru.evgenykuzakov.domain.use_case

import ru.evgenykuzakov.domain.model.Statistic
import ru.evgenykuzakov.domain.model.VisitorType
import java.time.LocalDate

class GetVisitsPerDayUseCase {
    operator fun invoke(stats: List<Statistic>): Map<LocalDate, Int> {
        val visitsPerDay = mutableMapOf<LocalDate, Int>()
        stats
            .filter { it.type == VisitorType.VIEW }
            .forEach { stat ->
                stat.dates.forEach { date ->
                    visitsPerDay[date] = visitsPerDay.getOrDefault(date, 0) + 1
                }
            }
        return visitsPerDay
    }
}