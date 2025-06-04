package ru.evgenykuzakov.rik_tt

import android.app.Application
import org.koin.android.ext.koin.androidContext
import ru.evgenykuzakov.rik_tt.di.initKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@App)
        }
    }
}