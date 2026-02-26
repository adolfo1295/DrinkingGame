package com.ac.drinkinggame.di

import com.ac.drinkinggame.data.remote.GameApiService
import com.ac.drinkinggame.data.remote.createKtorClient
import com.ac.drinkinggame.data.repository.GameRepositoryImpl
import com.ac.drinkinggame.domain.repository.GameRepository
import com.ac.drinkinggame.domain.usecase.GetCardsByCategoryUseCase
import com.ac.drinkinggame.domain.usecase.GetCategoriesUseCase
import com.ac.drinkinggame.ui.screens.game.GameViewModel
import com.ac.drinkinggame.ui.screens.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    single { createKtorClient() }
    single { GameApiService(get()) }
}

val appModule = module {
    single<GameRepository> { GameRepositoryImpl(get()) }
    
    factory { GetCategoriesUseCase(get()) }
    factory { GetCardsByCategoryUseCase(get()) }

    viewModel { HomeViewModel(get()) }
    viewModel { GameViewModel(get()) }
}
