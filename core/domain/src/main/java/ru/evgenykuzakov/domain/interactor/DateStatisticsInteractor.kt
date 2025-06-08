package ru.evgenykuzakov.domain.interactor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import ru.evgenykuzakov.common.Resource
import ru.evgenykuzakov.domain.model.ByDateStatisticFilter
import ru.evgenykuzakov.domain.model.DateStatistic
import ru.evgenykuzakov.domain.use_case.GetDateStatisticsUseCase
import ru.evgenykuzakov.domain.use_case.GetStatisticsUseCase
import ru.evgenykuzakov.domain.use_case.GetUsersUseCase
import java.time.LocalDate

class DateStatisticsInteractor(
    private val getUsersUseCase: GetUsersUseCase,
    private val getStatisticsUseCase: GetStatisticsUseCase,
    private val getVisitorsByDateUseCase: GetDateStatisticsUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(
        nowDate: LocalDate,
        filter: ByDateStatisticFilter
    ): Flow<Resource<List<DateStatistic>>> {
        return combine(
            getUsersUseCase(),
            getStatisticsUseCase()
        ) { usersRes, statsRes ->
            when {
                usersRes is Resource.Success && statsRes is Resource.Success -> {
                    getVisitorsByDateUseCase(
                        nowDate = nowDate,
                        filter = filter,
                        stats = statsRes.data
                    ).map { Resource.Success(it) }
                }

                usersRes is Resource.Error -> flowOf<Resource<List<DateStatistic>>>(Resource.Error(usersRes.message))
                statsRes is Resource.Error -> flowOf(Resource.Error(statsRes.message))
                else -> flowOf(Resource.Loading())
            }
        }.flatMapLatest { it }
    }
}