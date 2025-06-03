package ru.evgenykuzakov.data.model.user_dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("age")
    val age: Int,
    @SerialName("files")
    val files: List<FileDto>,
    @SerialName("id")
    val id: Int,
    @SerialName("isOnline")
    val isOnline: Boolean,
    @SerialName("sex")
    val sex: String,
    @SerialName("username")
    val username: String
)