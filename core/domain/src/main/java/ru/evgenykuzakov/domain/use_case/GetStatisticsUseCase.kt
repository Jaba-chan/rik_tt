package ru.evgenykuzakov.domain.use_case

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import ru.evgenykuzakov.common.Resource
import ru.evgenykuzakov.domain.AppRepository
import ru.evgenykuzakov.domain.model.Statistic

class GetStatisticsUseCase(
    private val repository: AppRepository
) {
    operator fun invoke(): Flow<Resource<List<Statistic>>> =
        flow {
            emit(Resource.Loading())
            emit(Resource.Success(repository.getStatistics()))
        }.catch { e ->
            println("GetUsersUseCase" + e.message)
            emit(Resource.Error(message = e.message))
        }
}