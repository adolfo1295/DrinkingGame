package com.ac.drinkinggame.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.ac.drinkinggame.data.repository.GameRepositoryImpl
import com.ac.drinkinggame.data.repository.PlayerRepositoryImpl
import com.ac.drinkinggame.domain.repository.GameRepository
import com.ac.drinkinggame.domain.repository.PlayerRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private val Context.dataStore by preferencesDataStore(name = "settings")

val dataModule = module {
    single { androidContext().dataStore }
    singleOf(::GameRepositoryImpl) { bind<GameRepository>() }
    singleOf(::PlayerRepositoryImpl) { bind<PlayerRepository>() }
}
