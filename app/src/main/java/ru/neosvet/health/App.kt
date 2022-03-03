package ru.neosvet.health

import android.app.Application
import org.koin.core.context.startKoin
import ru.neosvet.health.di.appModule

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(appModule)
        }
    }
}