package ru.evgenykuzakov.rik_tt.di

import org.koin.dsl.module
import ru.evgenykuzakov.domain.interactor.AgeSexStatisticInteractor
import ru.evgenykuzakov.domain.interactor.DateStatisticsInteractor
import ru.evgenykuzakov.domain.interactor.MostOftenVisitorsInteractor
import ru.evgenykuzakov.domain.interactor.VisitorsByTypeInteractor
import ru.evgenykuzakov.domain.use_case.GetAgeSexStatisticUseCase
import ru.evgenykuzakov.domain.use_case.GetDateStatisticsUseCase
import ru.evgenykuzakov.domain.use_case.GetMostOftenVisitorsUseCase
import ru.evgenykuzakov.domain.use_case.GetStatisticsUseCase
import ru.evgenykuzakov.domain.use_case.GetUsersUseCase
import ru.evgenykuzakov.domain.use_case.GetVisitorsByTypeUseCase
import ru.evgenykuzakov.domain.use_case.GetVisitsPerDayUseCase

val domainModule = module {
    single { GetUsersUseCase(get()) }
    single { GetStatisticsUseCase(get()) }
    single { GetVisitsPerDayUseCase() }

    single { GetAgeSexStatisticUseCase() }
    single { GetDateStatisticsUseCase(get()) }
    single { GetMostOftenVisitorsUseCase() }
    single { GetVisitorsByTypeUseCase() }

    single { AgeSexStatisticInteractor(get(), get(), get()) }
    single { DateStatisticsInteractor(get(), get(), get()) }
    single { MostOftenVisitorsInteractor(get(), get(), get()) }
    single { VisitorsByTypeInteractor(get(), get(), get()) }
}