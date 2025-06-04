package ru.evgenykuzakov.data.mapper

import ru.evgenykuzakov.data.model.statistic_dto.StatisticDto
import ru.evgenykuzakov.domain.model.Statistic
import ru.evgenykuzakov.domain.model.VisitorType

fun StatisticDto.toDomain() = Statistic(
    userId = userId,
    type = VisitorType.valueOf(type),
    dates = dates
)