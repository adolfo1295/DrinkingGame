package com.ac.drinkinggame.di

import com.ac.drinkinggame.ui.screens.game.GameViewModel
import com.ac.drinkinggame.ui.screens.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
  viewModelOf(::HomeViewModel)
  viewModelOf(::GameViewModel)
}
