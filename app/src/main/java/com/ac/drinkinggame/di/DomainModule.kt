package com.ac.drinkinggame.di

import com.ac.drinkinggame.domain.usecase.*
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
  factoryOf(::GetCategoriesUseCase)
  factoryOf(::SyncCategoriesUseCase)
  factoryOf(::GetCardsByCategoryUseCase)
  factoryOf(::SyncCardsByCategoryUseCase)
}
