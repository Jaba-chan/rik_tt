package ru.evgenykuzakov.domain.model

data class AgeSexStatistic(
    val ageGroup: AgeGroups,
    val sex: Sex,
    val visitorsCount: Int
)

enum class ByAgeSexStatisticFilter {
    DAY,
    WEEK,
    MONTH,
    ALL
}

enum class AgeGroups(val range: IntRange) {
    GROUP_0_18(0..18),
    GROUP_18_21(18..21),
    GROUP_22_25(22..25),
    GROUP_26_30(26..30),
    GROUP_31_35(31..35),
    GROUP_36_40(36..40),
    GROUP_50plus(50..200)
}