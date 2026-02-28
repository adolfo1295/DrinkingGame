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
import java.util.Locale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GameRepositoryImpl(
  private val apiService: GameApiService,
  private val categoryDao: CategoryDao,
  private val cardDao: CardDao
) : GameRepository {

  private val isEnglish get() = Locale.getDefault().language == "en"

  override fun getCategories(): Flow<List<Category>> {
    return categoryDao.getCategories().map { entities ->
      entities.map { it.toDomain(isEnglish) }
    }
  }

  override fun getCategoryById(categoryId: String): Flow<Category?> {
    return categoryDao.getCategoryById(categoryId).map { it?.toDomain(isEnglish) }
  }

  override fun getCardsByCategory(categoryId: String): Flow<List<GameCard>> {
    return cardDao.getCardsByCategory(categoryId).map { entities ->
      entities.map { entity ->
        val dto = Json.decodeFromString<CardDto>(entity.contentJson)
        dto.toDomain(isEnglish)
      }
    }
  }

  override suspend fun syncCategories(): Result<Unit> {
    return apiService.getCategories().map { dtos ->
      categoryDao.insertCategories(dtos.map { dto ->
        val category = dto.toDomain(isEnglish)
        category.toEntity().copy(nameEn = dto.nameEn) // Persistimos el nombre en inglés en Room
      })
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

  override suspend fun isFeatureFlagActive(id: String): Boolean {
    return apiService.getFeatureFlag(id).map { it.firstOrNull()?.isActive ?: false }.getOrDefault(false)
  }
}

fun CategoryDto.toDomain(isEnglish: Boolean = false) = Category(
  id = id,
  name = if (isEnglish && nameEn != null) nameEn else name,
  isPremium = isPremium,
  price = price,
  version = version,
  styleKey = styleKey
)

fun CardDto.toDomain(isEnglish: Boolean = false): GameCard {
  return when (val c = content) {
    is TriviaContentDto -> GameCard.Trivia(
      id = id,
      categoryId = categoryId,
      question = if (isEnglish && c.questionEn != null) c.questionEn else c.question,
      answer = if (isEnglish && c.answerEn != null) c.answerEn else c.answer,
      options = if (isEnglish && c.optionsEn != null) c.optionsEn else c.options,
      penalty = c.penalty
    )
    is ChallengeContentDto -> GameCard.Challenge(
      id = id,
      categoryId = categoryId,
      title = if (isEnglish && c.titleEn != null) c.titleEn else c.title,
      description = if (isEnglish && c.descriptionEn != null) c.descriptionEn else c.description,
      penalty = c.penalty
    )
    is RuleContentDto -> GameCard.Rule(
      id = id,
      categoryId = categoryId,
      title = if (isEnglish && c.titleEn != null) c.titleEn else c.title,
      rule = if (isEnglish && c.ruleEn != null) c.ruleEn else c.rule,
      duration = if (isEnglish && c.durationEn != null) c.durationEn else c.duration
    )
  }
}
