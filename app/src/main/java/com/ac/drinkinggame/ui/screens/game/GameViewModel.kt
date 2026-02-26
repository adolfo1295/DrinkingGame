package com.ac.drinkinggame.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.domain.model.GameCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface GameState {
    object CategorySelection : GameState
    object Loading : GameState
    data class Success(val currentCard: GameCard, val hasMore: Boolean) : GameState
    object Empty : GameState
    data class Error(val message: String) : GameState
}

sealed interface GameIntent {
    data class SelectCategory(val category: Category) : GameIntent
    object NextCard : GameIntent
    object Restart : GameIntent
}

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<GameState>(GameState.Loading)
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    private var cardList = mutableListOf<GameCard>()
    private var currentIndex = 0

    fun onIntent(intent: GameIntent) {
        when (intent) {
            is GameIntent.SelectCategory -> startGame(intent.category)
            GameIntent.NextCard -> nextCard()
            GameIntent.Restart -> _uiState.update { GameState.CategorySelection }
        }
    }

    private fun startGame(category: Category) {
        viewModelScope.launch {
            _uiState.update { GameState.Loading }
            delay(1000) // Simular carga
            
            // Generar mock data basado en categoría
            cardList = getMockCards(category).toMutableList()
            currentIndex = 0
            updateState()
        }
    }

    private fun nextCard() {
        if (currentIndex < cardList.size - 1) {
            currentIndex++
            updateState()
        } else {
            _uiState.update { GameState.Empty }
        }
    }

    private fun updateState() {
        _uiState.update {
            if (cardList.isEmpty()) {
                GameState.Empty
            } else {
                GameState.Success(
                    currentCard = cardList[currentIndex],
                    hasMore = currentIndex < cardList.size - 1
                )
            }
        }
    }

    private fun getMockCards(category: Category): List<GameCard> {
        val allCards = listOf(
            GameCard.Trivia("¿Capital de Italia?", "Roma", "Falla y bebe", 2),
            GameCard.Trivia("¿2 + 2?", "4", "Falla y bebe", 1),
            GameCard.Challenge("Grita fuerte", true, "Grita '¡Soy el rey!', si no, bebes", 3),
            GameCard.Challenge("Abrazo grupal", false, "Todos se abrazan, el último bebe", 1),
            GameCard.Rule("Manos arriba", "El último en subir las manos bebe", 2)
        )

        return when (category) {
            Category.TRIVIA -> allCards.filterIsInstance<GameCard.Trivia>()
            Category.CHALLENGES -> allCards.filterIsInstance<GameCard.Challenge>().filter { it.isLocoMode }
            Category.FAMILIAR -> allCards.filterIsInstance<GameCard.Challenge>().filter { !it.isLocoMode }
            Category.MIXED -> allCards.shuffled()
        }
    }
}
