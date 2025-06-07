package ru.evgenykuzakov.domain.model

data class Statistic(
    val dates: List<Int>,
    val type: VisitorType,
    val userId: Int
)