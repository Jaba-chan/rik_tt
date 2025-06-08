package ru.evgenykuzakov.data.mapper

import ru.evgenykuzakov.data.model.statistic_dto.StatisticDto
import ru.evgenykuzakov.domain.model.Statistic
import ru.evgenykuzakov.domain.model.VisitorType
import java.time.LocalDate

fun StatisticDto.toDomain() = Statistic(
    userId = userId,
    type = VisitorType.entries.first { it.type == type },
    dates = dates.map { it.toLocalDate() }
)

private fun Int.toLocalDate(): LocalDate {
    val str = this.toString()
    val dayLength = str.length - 6
    val day = str.substring(0, dayLength).toInt()
    val month = str.substring(dayLength, dayLength + 2).toInt()
    val year = str.substring(dayLength + 2).toInt()

    return LocalDate.of(year, month, day)
}