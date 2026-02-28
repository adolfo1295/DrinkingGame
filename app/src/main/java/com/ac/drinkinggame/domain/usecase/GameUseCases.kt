package com.ac.drinkinggame.domain.usecase

import com.ac.drinkinggame.domain.repository.GameRepository

class GetCategoriesUseCase(private val repository: GameRepository) {
  operator fun invoke() = repository.getCategories()
}

class SyncCategoriesUseCase(private val repository: GameRepository) {
  suspend operator fun invoke() = repository.syncCategories()
}

class GetCategoryByIdUseCase(private val repository: GameRepository) {
  operator fun invoke(categoryId: String) = repository.getCategoryById(categoryId)
}

class GetCardsByCategoryUseCase(private val repository: GameRepository) {
  operator fun invoke(categoryId: String) = repository.getCardsByCategory(categoryId)
}

class SyncCardsByCategoryUseCase(private val repository: GameRepository) {
  suspend operator fun invoke(categoryId: String) = repository.syncCardsByCategory(categoryId)
}
