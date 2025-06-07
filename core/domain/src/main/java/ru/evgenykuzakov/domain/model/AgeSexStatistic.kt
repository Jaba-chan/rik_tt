package ru.evgenykuzakov.domain.model

data class AgeSexStatistic(
    val ageGroup: AgeGroups,
    val sex: Sex,
    val visitorsCount: Int
)

enum class ByAgeSexStatisticFilter {
    DAY, WEEK, MONTH, ALL
}