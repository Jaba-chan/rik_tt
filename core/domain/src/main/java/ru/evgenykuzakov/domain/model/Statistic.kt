package ru.evgenykuzakov.domain.model

import java.time.LocalDate

data class Statistic(
    val dates: List<LocalDate>,
    val type: VisitorType,
    val userId: Int
)