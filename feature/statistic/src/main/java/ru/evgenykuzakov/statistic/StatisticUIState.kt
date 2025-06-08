package ru.evgenykuzakov.statistic

import ru.evgenykuzakov.common.Resource
import ru.evgenykuzakov.domain.model.AgeSexStatisticResult
import ru.evgenykuzakov.domain.model.ByAgeSexStatisticFilter
import ru.evgenykuzakov.domain.model.ByDateStatisticFilter
import ru.evgenykuzakov.domain.model.DateStatistic
import ru.evgenykuzakov.domain.model.User
import ru.evgenykuzakov.domain.model.VisitorsByTypeResult

data class StatisticUIState(
    val dateStatistic: Resource<List<DateStatistic>> = Resource.Loading(),
    val visitorsByType: Resource<VisitorsByTypeResult> = Resource.Loading(),
    val mostOftenVisitors: Resource<List<User>> = Resource.Loading(),
    val ageSexStatistic: Resource<AgeSexStatisticResult> = Resource.Loading(),

    val dateFilter: ByDateStatisticFilter = ByDateStatisticFilter.DAY,
    val ageSexFilter: ByAgeSexStatisticFilter = ByAgeSexStatisticFilter.DAY,
    val scrollPosition: Int = 0,
)