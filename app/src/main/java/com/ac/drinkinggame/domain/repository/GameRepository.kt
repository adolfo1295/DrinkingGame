package com.ac.drinkinggame.domain.repository

import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.domain.model.GameCard

interface GameRepository {
    suspend fun getCategories(): Result<List<Category>>
    suspend fun getCardsByCategory(categoryId: String): Result<List<GameCard>>
}
