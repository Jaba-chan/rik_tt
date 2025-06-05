package ru.evgenykuzakov.domain.model

data class StatisticUIState(
    val visitorsByDate: List<VisitorsByDate>,
    val visitors: Int,
    val subscribers: Int,
    val unsubscribers: Int,
    val mostOftenVisitors: List<User>,
    val ageSexStat: List<AgeSexStatistic>,
    val menCount: Int,
    val womenCount: Int
)