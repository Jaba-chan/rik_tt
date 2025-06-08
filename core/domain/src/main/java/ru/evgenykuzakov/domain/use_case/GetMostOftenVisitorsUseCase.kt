package ru.evgenykuzakov.domain.use_case

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.evgenykuzakov.domain.model.Statistic
import ru.evgenykuzakov.domain.model.User
import ru.evgenykuzakov.domain.model.VisitorType

class GetMostOftenVisitorsUseCase {
    operator fun invoke(users: List<User>, stats: List<Statistic>): Flow<List<User>> = flow {
        val result = users.associateWith { user ->
            stats
                .filter { it.userId == user.id && it.type == VisitorType.VIEW }
                .sumOf { it.dates.size }
        }
            .entries
            .sortedByDescending { it.value }
            .take(3)
            .map { it.key }

        emit(result)
    }.flowOn(Dispatchers.Default)
}