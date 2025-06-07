package ru.evgenykuzakov.domain.model

import ru.evgenykuzakov.common.Resource

data class StatisticUIState(
    val dateStatistic: Resource<List<DateStatistic>> = Resource.Loading(),
    val visitorsByType: Resource<VisitorsByTypeResult> = Resource.Loading(),
    val mostOftenVisitors: Resource<List<User>> = Resource.Loading(),
    val ageSexStatistic: Resource<AgeSexStatisticResult> = Resource.Loading(),

    val dateFilter: ByDateStatisticFilter = ByDateStatisticFilter.DAY,
    val ageSexFilter: ByAgeSexStatisticFilter = ByAgeSexStatisticFilter.DAY
)