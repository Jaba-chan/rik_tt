package ru.evgenykuzakov.domain.use_case

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.evgenykuzakov.domain.model.Statistic
import ru.evgenykuzakov.domain.model.User
import ru.evgenykuzakov.domain.model.VisitorType
import ru.evgenykuzakov.domain.model.VisitorsByType
import ru.evgenykuzakov.domain.model.VisitorsByTypeResult

class GetVisitorsByTypeUseCase {
    operator fun invoke(
        users: List<User>,
        stats: List<Statistic>
    ): Flow<VisitorsByTypeResult> = flow {
        val grouped = VisitorType.entries.associateWith { type ->
            val count = users.associateWith { user ->
                stats
                    .filter { it.userId == user.id && it.type == type }
                    .sumOf { it.dates.size }
            }
                .values
                .sumOf { it }

            VisitorsByType(type = type, count = count)
        }

        val result = VisitorsByTypeResult(
            view = grouped[VisitorType.VIEW] ?: VisitorsByType(VisitorType.VIEW, 0),
            subscribers = grouped[VisitorType.SUBSCRIPTION] ?: VisitorsByType(VisitorType.SUBSCRIPTION, 0),
            unsubscribers = grouped[VisitorType.UNSUBSCRIPTION] ?: VisitorsByType(VisitorType.UNSUBSCRIPTION, 0)
        )

        emit(result)
    }.flowOn(Dispatchers.Default)
}
