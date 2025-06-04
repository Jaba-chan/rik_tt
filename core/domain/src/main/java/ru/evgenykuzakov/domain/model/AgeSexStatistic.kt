package ru.evgenykuzakov.domain.model

data class AgeSexStatistic(
    val ageGroup: AgeGroups,
    val sex: Sex,
    val visitorsCount: Int
)