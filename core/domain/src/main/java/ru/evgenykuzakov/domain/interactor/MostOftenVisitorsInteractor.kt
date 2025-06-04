package ru.evgenykuzakov.domain.interactor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import ru.evgenykuzakov.common.Resource
import ru.evgenykuzakov.domain.model.MostOftenVisitors
import ru.evgenykuzakov.domain.use_case.GetStatisticsUseCase
import ru.evgenykuzakov.domain.use_case.GetUsersUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class MostOftenVisitorsInteractor(
    private val getUsersUseCase: GetUsersUseCase,
    private val getStatisticsUseCase: GetStatisticsUseCase
) {

    operator fun invoke(): Flow<Resource<List<MostOftenVisitors>>> {
        return getUsersUseCase().flatMapLatest { usersRes ->
            when (usersRes) {
                is Resource.Loading -> flowOf(Resource.Loading())
                is Resource.Error -> flowOf(Resource.Error(usersRes.message))
                is Resource.Success -> {

                    getStatisticsUseCase().map { statRes->
                        when (statRes) {
                            is Resource.Loading -> Resource.Loading()
                            is Resource.Error -> Resource.Error(statRes.message)
                            is Resource.Success -> {
                                val users = usersRes.data
                                val stats = statRes.data

                                val visitors = users.map { user ->
                                    MostOftenVisitors(user,
                                        stats
                                            .filter { it.userId == user.id }
                                            .sumOf { it.dates.size }
                                    )
                                }
                                    .sortedBy { it.position }
                                    .slice(0..3)

                                Resource.Success(visitors)
                            }
                        }
                    }
                }
            }
        }
    }
}
