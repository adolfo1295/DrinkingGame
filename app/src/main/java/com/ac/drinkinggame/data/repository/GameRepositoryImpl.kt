package com.ac.drinkinggame.data.repository

import com.ac.drinkinggame.data.local.dao.CardDao
import com.ac.drinkinggame.data.local.dao.CategoryDao
import com.ac.drinkinggame.data.local.entity.CardEntity
import com.ac.drinkinggame.data.local.entity.toDomain
import com.ac.drinkinggame.data.local.entity.toEntity
import com.ac.drinkinggame.data.remote.GameApiService
import com.ac.drinkinggame.data.remote.dto.*
import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.domain.model.GameCard
import com.ac.drinkinggame.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GameRepositoryImpl(
  private val apiService: GameApiService,
  private val categoryDao: CategoryDao,
  private val cardDao: CardDao
) : GameRepository {

  override fun getCategories(): Flow<List<Category>> {
    return categoryDao.getCategories().map { entities ->
      entities.map { it.toDomain() }
    }
  }

  override fun getCardsByCategory(categoryId: String): Flow<List<GameCard>> {
    return cardDao.getCardsByCategory(categoryId).map { entities ->
      entities.map { entity ->
        val dto = Json.decodeFromString<CardDto>(entity.contentJson)
        dto.toDomain()
      }
    }
  }

  override suspend fun syncCategories(): Result<Unit> {
    return apiService.getCategories().map { dtos ->
      categoryDao.insertCategories(dtos.map { it.toDomain().toEntity() })
      Unit
    }
  }

  override suspend fun syncCardsByCategory(categoryId: String): Result<Unit> {
    return apiService.getCardsByCategory(categoryId).map { dtos ->
      val entities = dtos.map { dto ->
        CardEntity(
          id = dto.id,
          categoryId = dto.categoryId,
          type = dto.type,
          contentJson = Json.encodeToString(dto)
        )
      }
      cardDao.insertCards(entities)
      Unit
    }
  }
}

// Mappers faltantes que se perdieron en el refactor
fun CategoryDto.toDomain() = Category(
  id = id,
  name = name,
  isPremium = isPremium,
  price = price,
  version = version
)

fun CardDto.toDomain(): GameCard {
  return when (val c = content) {
    is TriviaContentDto -> GameCard.Trivia(
      id = id,
      categoryId = categoryId,
      question = c.question,
      answer = c.answer,
      options = c.options,
      penalty = c.penalty
    )
    is ChallengeContentDto -> GameCard.Challenge(
      id = id,
      categoryId = categoryId,
      title = c.title,
      description = c.description,
      penalty = c.penalty
    )
    is RuleContentDto -> GameCard.Rule(
      id = id,
      categoryId = categoryId,
      title = c.title,
      rule = c.rule,
      duration = c.duration
    )
  }
}
