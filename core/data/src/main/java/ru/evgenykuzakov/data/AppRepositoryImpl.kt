package ru.evgenykuzakov.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.json.Json
import ru.evgenykuzakov.data.mapper.toDomain
import ru.evgenykuzakov.data.model.statistic_dto.StatisticsDto
import ru.evgenykuzakov.data.model.user_dto.UsersDto
import ru.evgenykuzakov.domain.AppRepository
import ru.evgenykuzakov.domain.model.Statistic
import ru.evgenykuzakov.domain.model.User

class AppRepositoryImpl(
    private val client: HttpClient
): AppRepository {

    override suspend fun getUsers(): List<User> {
        val rawJson: String = client
            .get("$BASE_URL/users/")
            .body()
        if (rawJson.trim() == "null") return emptyList()
        val dto: UsersDto = Json.decodeFromString(rawJson)
        return dto.users.map { it.toDomain() }
    }

    override suspend fun getStatistics(): List<Statistic> {
        val rawJson: String = client
                .get("$BASE_URL/statistics/")
            .body()
        if (rawJson.trim() == "null") return emptyList()
        val dto: StatisticsDto = Json.decodeFromString(rawJson)
        return dto.statistics.map { it.toDomain() }
    }

    companion object {
        const val BASE_URL = "http://test.rikmasters.ru/api"
    }
}