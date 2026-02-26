package com.ac.drinkinggame.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ac.drinkinggame.domain.model.GameCard
import com.ac.drinkinggame.domain.usecase.GetCardsByCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface GameState {
  data object Loading : GameState
  data class Success(val currentCard: GameCard, val hasMore: Boolean) : GameState
  data object Empty : GameState
  data class Error(val message: String) : GameState
}

sealed interface GameIntent {
  data class LoadCards(val categoryId: String) : GameIntent
  data object NextCard : GameIntent
}

class GameViewModel(
  private val getCardsByCategoryUseCase: GetCardsByCategoryUseCase
) : ViewModel() {

  private val _uiState = MutableStateFlow<GameState>(GameState.Loading)
  val uiState: StateFlow<GameState> = _uiState.asStateFlow()

  private var cardList = listOf<GameCard>()
  private var currentIndex = 0

  fun onIntent(intent: GameIntent) {
    when (intent) {
      is GameIntent.LoadCards -> loadCards(intent.categoryId)
      GameIntent.NextCard -> nextCard()
    }
  }

  private fun loadCards(categoryId: String) {
    viewModelScope.launch {
      _uiState.update { GameState.Loading }

      getCardsByCategoryUseCase(categoryId)
        .onSuccess { cards ->
          if (cards.isEmpty()) {
            _uiState.update { GameState.Empty }
          } else {
            cardList = cards
            currentIndex = 0
            updateState()
          }
        }
        .onFailure { error ->
          _uiState.update { GameState.Error(error.message ?: "Unknown Error") }
        }
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
      GameState.Success(
        currentCard = cardList[currentIndex],
        hasMore = currentIndex < cardList.size - 1
      )
    }
  }
}
