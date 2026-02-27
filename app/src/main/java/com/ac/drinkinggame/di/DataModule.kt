package com.ac.drinkinggame.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.ac.drinkinggame.data.local.AppDatabase
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

  // Room
  single {
    Room.databaseBuilder(
      androidContext(),
      AppDatabase::class.java,
      "drinking_game.db"
    ).build()
  }
  single { get<AppDatabase>().categoryDao() }
  single { get<AppDatabase>().cardDao() }

  singleOf(::GameRepositoryImpl) { bind<GameRepository>() }
  singleOf(::PlayerRepositoryImpl) { bind<PlayerRepository>() }
}

