package ru.evgenykuzakov.domain.interactor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import ru.evgenykuzakov.common.Resource
import ru.evgenykuzakov.domain.model.VisitorsByType
import ru.evgenykuzakov.domain.model.VisitorsByTypeResult
import ru.evgenykuzakov.domain.use_case.GetStatisticsUseCase
import ru.evgenykuzakov.domain.use_case.GetUsersUseCase
import ru.evgenykuzakov.domain.use_case.GetVisitorsByTypeUseCase


class VisitorsByTypeInteractor(
    private val getUsersUseCase: GetUsersUseCase,
    private val getStatisticsUseCase: GetStatisticsUseCase,
    private val getVisitorsByTypeUseCase: GetVisitorsByTypeUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Resource<VisitorsByTypeResult>> {
        return combine(
            getUsersUseCase(),
            getStatisticsUseCase()
        ) { usersRes, statsRes ->
            when {
                usersRes is Resource.Success && statsRes is Resource.Success -> {
                    getVisitorsByTypeUseCase(
                        users = usersRes.data,
                        stats = statsRes.data
                    ).map { Resource.Success(it) }
                }
                usersRes is Resource.Error -> flowOf<Resource<VisitorsByTypeResult>>(
                    Resource.Error(
                        usersRes.message
                    )
                )
                statsRes is Resource.Error -> flowOf(Resource.Error(statsRes.message))
                else -> flowOf(Resource.Loading())
            }
        }.flatMapLatest { it }
    }
}
