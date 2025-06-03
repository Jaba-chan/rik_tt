package ru.evgenykuzakov.data.mapper

import ru.evgenykuzakov.data.model.user_dto.FileDto
import ru.evgenykuzakov.domain.model.File

fun FileDto.toDomain() = File(
    id = id,
    type = type,
    url = url
)