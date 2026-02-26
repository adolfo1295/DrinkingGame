package com.ac.drinkinggame.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.ac.drinkinggame.data.remote.GameApiService
import com.ac.drinkinggame.data.remote.createKtorClient
import com.ac.drinkinggame.data.repository.GameRepositoryImpl
import com.ac.drinkinggame.data.repository.PlayerRepositoryImpl
import com.ac.drinkinggame.domain.repository.GameRepository
import com.ac.drinkinggame.domain.repository.PlayerRepository
import com.ac.drinkinggame.domain.usecase.GetCardsByCategoryUseCase
import com.ac.drinkinggame.domain.usecase.GetCategoriesUseCase
import com.ac.drinkinggame.ui.screens.game.GameViewModel
import com.ac.drinkinggame.ui.screens.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val Context.dataStore by preferencesDataStore(name = "settings")

val networkModule = module {
    single { createKtorClient() }
    single { GameApiService(get()) }
}

val appModule = module {
    single { androidContext().dataStore }
    single<GameRepository> { GameRepositoryImpl(get()) }
    single<PlayerRepository> { PlayerRepositoryImpl(get()) }
    
    factory { GetCategoriesUseCase(get()) }
    factory { GetCardsByCategoryUseCase(get()) }

    viewModel { HomeViewModel(get(), get()) }
    viewModel { GameViewModel(get(), get()) }
}
