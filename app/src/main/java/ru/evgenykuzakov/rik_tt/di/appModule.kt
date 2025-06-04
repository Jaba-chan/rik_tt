package ru.evgenykuzakov.rik_tt.di

import org.koin.dsl.module
import ru.evgenykuzakov.data.di.dataModule
import ru.evgenykuzakov.statistic.di.statisticFeatureModule

val appModule = module {
    includes(domainModule, dataModule, statisticFeatureModule)
}