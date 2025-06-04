package ru.evgenykuzakov.rik_tt.di

import org.koin.dsl.module
import ru.evgenykuzakov.domain.interactor.StatisticInteractor
import ru.evgenykuzakov.domain.use_case.GetStatisticsUseCase
import ru.evgenykuzakov.domain.use_case.GetUsersUseCase

val domainModule = module {
    single { GetUsersUseCase(get()) }
    single { GetStatisticsUseCase(get()) }
    single { StatisticInteractor(get(), get()) }
}