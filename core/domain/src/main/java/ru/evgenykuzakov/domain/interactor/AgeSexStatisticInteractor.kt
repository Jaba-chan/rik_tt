package ru.evgenykuzakov.domain.interactor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import ru.evgenykuzakov.common.Resource
import ru.evgenykuzakov.domain.model.AgeSexStatisticResult
import ru.evgenykuzakov.domain.model.ByAgeSexStatisticFilter
import ru.evgenykuzakov.domain.use_case.GetAgeSexStatisticUseCase
import ru.evgenykuzakov.domain.use_case.GetStatisticsUseCase
import ru.evgenykuzakov.domain.use_case.GetUsersUseCase
import java.time.LocalDate


class AgeSexStatisticInteractor(
    private val getUsersUseCase: GetUsersUseCase,
    private val getStatisticsUseCase: GetStatisticsUseCase,
    private val getSexAgeStatisticUseCase: GetAgeSexStatisticUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        nowDate: LocalDate,
        filter: ByAgeSexStatisticFilter
    ): Flow<Resource<AgeSexStatisticResult>> {
        return combine(
            getUsersUseCase(),
            getStatisticsUseCase()
        ) { usersRes, statsRes ->
            when {
                usersRes is Resource.Success && statsRes is Resource.Success -> {
                    getSexAgeStatisticUseCase(
                        nowDate = nowDate,
                        filter = filter,
                        users = usersRes.data,
                        stats = statsRes.data
                    ).map { Resource.Success(it) }
                }

                usersRes is Resource.Error -> flowOf<Resource<AgeSexStatisticResult>>(Resource.Error(usersRes.message))
                statsRes is Resource.Error -> flowOf(Resource.Error(statsRes.message))
                else -> flowOf(Resource.Loading())
            }
        }.flatMapLatest { it }
    }
}