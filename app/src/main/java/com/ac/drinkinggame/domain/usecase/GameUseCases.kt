package com.ac.drinkinggame.domain.usecase

import com.ac.drinkinggame.domain.repository.GameRepository

class GetCategoriesUseCase(private val repository: GameRepository) {
    suspend operator fun invoke() = repository.getCategories()
}

class GetCardsByCategoryUseCase(private val repository: GameRepository) {
    suspend operator fun invoke(categoryId: String) = repository.getCardsByCategory(categoryId)
}
