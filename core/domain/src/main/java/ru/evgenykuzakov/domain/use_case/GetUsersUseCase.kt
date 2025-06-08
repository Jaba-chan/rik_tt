package ru.evgenykuzakov.domain.use_case

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.evgenykuzakov.common.Resource
import ru.evgenykuzakov.domain.AppRepository
import ru.evgenykuzakov.domain.model.User

class GetUsersUseCase(
    private val repository: AppRepository,

) {
    operator fun invoke(): Flow<Resource<List<User>>> =
        flow {
            emit(Resource.Loading())
            emit(Resource.Success(repository.getUsers()))
        }.catch { e ->
            println("GetUsersUseCase" + e.message)
            emit(Resource.Error(message = e.message))
        }
            .flowOn(Dispatchers.IO)

}