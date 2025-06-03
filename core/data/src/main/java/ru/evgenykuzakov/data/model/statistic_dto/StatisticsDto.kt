package ru.evgenykuzakov.data.model.statistic_dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatisticsDto(
    @SerialName("statistics")
    val statistics: List<StatisticDto>
)