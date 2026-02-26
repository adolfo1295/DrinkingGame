package com.ac.drinkinggame.domain.model

sealed interface GameCard {
    val description: String
    val penalty: Int

    data class Trivia(
        val question: String,
        val answer: String,
        override val description: String,
        override val penalty: Int
    ) : GameCard

    data class Challenge(
        val title: String,
        val isLocoMode: Boolean,
        override val description: String,
        override val penalty: Int
    ) : GameCard

    data class Rule(
        val title: String,
        override val description: String,
        override val penalty: Int
    ) : GameCard
}
