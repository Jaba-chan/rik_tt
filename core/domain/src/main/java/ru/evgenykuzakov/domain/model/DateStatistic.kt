package ru.evgenykuzakov.domain.model

import java.time.LocalDate

data class DateStatistic(
    val date: LocalDate,
    val visitors: Int
)

enum class ByDateStatisticFilter {
    DAY,
    WEEK,
    MONTH
}