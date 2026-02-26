package com.ac.drinkinggame.domain.model

sealed interface GameCard {
    val id: String
    val categoryId: String

    data class Trivia(
        override val id: String,
        override val categoryId: String,
        val question: String,
        val answer: String,
        val options: List<String>?,
        val penalty: Int
    ) : GameCard

    data class Challenge(
        override val id: String,
        override val categoryId: String,
        val title: String,
        val description: String,
        val penalty: Int
    ) : GameCard

    data class Rule(
        override val id: String,
        override val categoryId: String,
        val title: String,
        val rule: String,
        val duration: String?
    ) : GameCard
}
