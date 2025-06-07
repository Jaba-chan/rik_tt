package ru.evgenykuzakov.domain.model

import ru.evgenykuzakov.common.Resource

data class StatisticUIState(
    val visitorsByDate: Resource<List<VisitorsByDate>> = Resource.Loading(),
    val visitorsByType: Resource<List<VisitorsByType>> = Resource.Loading(),
    val mostOftenVisitors: Resource<List<User>> = Resource.Loading(),
    val sexAgeStatistic: Resource<List<AgeSexStatisticResult>> = Resource.Loading(),

    val dateFilter: ByDateStatisticFilter = ByDateStatisticFilter.DAY,
    val ageSexFilter: ByAgeSexStatisticFilter = ByAgeSexStatisticFilter.DAY
)