package com.ac.drinkinggame

import android.app.Application
import com.ac.drinkinggame.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DrinkingGameApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@DrinkingGameApp)
            modules(
                networkModule,
                dataModule,
                domainModule,
                viewModelModule
            )
        }
    }
}
