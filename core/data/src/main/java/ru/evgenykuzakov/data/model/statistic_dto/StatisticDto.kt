package ru.evgenykuzakov.data.model.statistic_dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatisticDto(
    @SerialName("dates")
    val dates: List<Int>,
    @SerialName("type")
    val type: String,
    @SerialName("user_id")
    val userId: Int
)