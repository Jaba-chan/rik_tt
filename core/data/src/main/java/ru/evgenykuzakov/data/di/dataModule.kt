package ru.evgenykuzakov.data.di

import org.koin.dsl.bind
import org.koin.dsl.module
import ru.evgenykuzakov.data.AppRepositoryImpl
import ru.evgenykuzakov.domain.AppRepository
import ru.evgenykuzakov.network.di.networkModule

val dataModule = module {
    single{ AppRepositoryImpl(get())}.bind<AppRepository>()
    includes(networkModule)
}