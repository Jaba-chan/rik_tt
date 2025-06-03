package ru.evgenykuzakov.data.model.user_dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsersDto(
    @SerialName("users")
    val users: List<UserDto>
)