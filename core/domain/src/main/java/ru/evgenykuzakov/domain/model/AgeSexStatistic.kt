package ru.evgenykuzakov.domain.model

data class AgeSexStatisticResult(
    val stats: List<AgeSexStat>,
    val menCount: Int,
    val womenCount: Int
)

data class AgeSexStat(
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
    GROUP_0_18(0 until 18),
    GROUP_18_21(18 until 21),
    GROUP_22_25(22 until 25),
    GROUP_26_30(26 until 30),
    GROUP_31_35(31 until 35),
    GROUP_36_40(36 until 40),
    GROUP_40_50(40 until 50),
    GROUP_50PLUS(50 until Int.MAX_VALUE)
}
