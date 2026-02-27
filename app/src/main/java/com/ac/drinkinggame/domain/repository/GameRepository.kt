package com.ac.drinkinggame.domain.repository

import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.domain.model.GameCard
import kotlinx.coroutines.flow.Flow

interface GameRepository {
  fun getCategories(): Flow<List<Category>>
  fun getCardsByCategory(categoryId: String): Flow<List<GameCard>>
  suspend fun syncCategories(): Result<Unit>
  suspend fun syncCardsByCategory(categoryId: String): Result<Unit>
}
