package com.ac.drinkinggame.data.repository

import com.ac.drinkinggame.data.remote.GameApiService
import com.ac.drinkinggame.data.remote.dto.*
import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.domain.model.GameCard
import com.ac.drinkinggame.domain.repository.GameRepository

class GameRepositoryImpl(
    private val apiService: GameApiService
) : GameRepository {

    override suspend fun getCategories(): Result<List<Category>> {
        return apiService.getCategories().map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override suspend fun getCardsByCategory(categoryId: String): Result<List<GameCard>> {
        return apiService.getCardsByCategory(categoryId).map { dtos ->
            dtos.map { it.toDomain() }
        }
    }
}

fun CategoryDto.toDomain() = Category(
    id = id,
    name = name,
    isPremium = isPremium,
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
