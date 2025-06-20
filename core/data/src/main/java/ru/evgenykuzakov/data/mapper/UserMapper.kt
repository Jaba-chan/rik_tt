package ru.evgenykuzakov.data.mapper

import ru.evgenykuzakov.data.model.user_dto.UserDto
import ru.evgenykuzakov.domain.model.Sex
import ru.evgenykuzakov.domain.model.User

fun UserDto.toDomain() = User(
    id = id,
    username = username,
    sex = Sex.entries.first { it.sex == sex },
    age = age,
    isOnline = isOnline,
    files = files.map{ it.toDomain() }
)
