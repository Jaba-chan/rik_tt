package ru.evgenykuzakov.domain

import ru.evgenykuzakov.domain.model.Statistic
import ru.evgenykuzakov.domain.model.User

interface AppRepository {

    suspend fun getUsers(): List<User>

    suspend fun getStatistics(): List<Statistic>
}