package com.ac.drinkinggame.di

import com.ac.drinkinggame.domain.usecase.GetCardsByCategoryUseCase
import com.ac.drinkinggame.domain.usecase.GetCategoriesUseCase
import org.koin.dsl.module

val domainModule = module {
  factory { GetCategoriesUseCase(get()) }
  factory { GetCardsByCategoryUseCase(get()) }
}
