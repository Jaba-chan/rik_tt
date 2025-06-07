package ru.evgenykuzakov.domain.use_case

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.evgenykuzakov.domain.model.Statistic
import ru.evgenykuzakov.domain.model.User
import ru.evgenykuzakov.domain.model.VisitorType
import ru.evgenykuzakov.domain.model.VisitorsByType

class GetVisitorsByTypeUseCase {
    operator fun invoke(users: List<User>, stats: List<Statistic>): Flow<List<VisitorsByType>> = flow {
        val result = VisitorType.entries.map { type ->
            val count = users.associateWith { user ->
                stats
                    .filter { it.userId == user.id && it.type == type }
                    .sumOf { it.dates.size }
            }
                .values
                .sumOf { it }

            VisitorsByType(
                type = type,
                count = count
            )
        }

        emit(result)
    }.flowOn(Dispatchers.Default)
}