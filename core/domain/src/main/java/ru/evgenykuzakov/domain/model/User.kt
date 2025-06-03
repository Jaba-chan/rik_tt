package ru.evgenykuzakov.domain.model

data class User(
    val age: Int,
    val files: List<File>,
    val id: Int,
    val isOnline: Boolean,
    val sex: String,
    val username: String
)