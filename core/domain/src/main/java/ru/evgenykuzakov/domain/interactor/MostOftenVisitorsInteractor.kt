package ru.evgenykuzakov.domain.interactor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import ru.evgenykuzakov.common.Resource
import ru.evgenykuzakov.domain.model.User
import ru.evgenykuzakov.domain.use_case.GetMostOftenVisitorsUseCase
import ru.evgenykuzakov.domain.use_case.GetStatisticsUseCase
import ru.evgenykuzakov.domain.use_case.GetUsersUseCase

class MostOftenVisitorsInteractor(
    private val getUsersUseCase: GetUsersUseCase,
    private val getStatisticsUseCase: GetStatisticsUseCase,
    private val getMostOftenVisitorsUseCase: GetMostOftenVisitorsUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Resource<List<User>>> {
        return combine(
            getUsersUseCase(),
            getStatisticsUseCase()
        ) { usersRes, statsRes ->
            when {
                usersRes is Resource.Success && statsRes is Resource.Success -> {
                    getMostOftenVisitorsUseCase(
                        users = usersRes.data,
                        stats = statsRes.data
                    ).map { Resource.Success(it) }
                }
                usersRes is Resource.Error -> flowOf<Resource<List<User>>>(Resource.Error(usersRes.message))
                statsRes is Resource.Error -> flowOf(Resource.Error(statsRes.message))
                else -> flowOf(Resource.Loading())
            }
        }.flatMapLatest { it }
    }
}