package ru.evgenykuzakov.statistic.di

import org.koin.dsl.module
import ru.evgenykuzakov.statistic.StatisticScreenViewModel

val statisticFeatureModule = module {
    single { StatisticScreenViewModel(get(), get(), get(), get()) }
}