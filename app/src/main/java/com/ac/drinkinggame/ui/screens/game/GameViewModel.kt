package com.ac.drinkinggame.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ac.drinkinggame.domain.model.GameCard
import com.ac.drinkinggame.domain.model.Player
import com.ac.drinkinggame.domain.repository.PlayerRepository
import com.ac.drinkinggame.domain.usecase.GetCardsByCategoryUseCase
import com.ac.drinkinggame.domain.usecase.SyncCardsByCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface GameState {
  data object Loading : GameState
  data class Success(
    val currentCard: GameCard,
    val currentPlayer: Player?,
    val hasMore: Boolean
  ) : GameState

  data object Empty : GameState
  data class Error(val message: String) : GameState
}

sealed interface GameIntent {
  data class LoadCards(val categoryId: String) : GameIntent
  data object NextCard : GameIntent
}

class GameViewModel(
  private val getCardsByCategoryUseCase: GetCardsByCategoryUseCase,
  private val syncCardsByCategoryUseCase: SyncCardsByCategoryUseCase,
  private val playerRepository: PlayerRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow<GameState>(GameState.Loading)
  val uiState: StateFlow<GameState> = _uiState.asStateFlow()

  private var cardList = listOf<GameCard>()
  private var players = listOf<Player>()
  private var currentIndex = 0
  private var playerIndex = 0

  fun onIntent(intent: GameIntent) {
    when (intent) {
      is GameIntent.LoadCards -> loadCards(intent.categoryId)
      GameIntent.NextCard -> nextCard()
    }
  }

  private fun loadCards(categoryId: String) {
    viewModelScope.launch {
      _uiState.update { GameState.Loading }

      // 1. Obtener jugadores
      players = playerRepository.getPlayers().first()

      // 2. Intentar sincronizar primero para tener datos frescos
      syncCardsByCategoryUseCase(categoryId)

      // 3. Obtener datos de Room (ya sincronizados o cache) una sola vez
      val cards = getCardsByCategoryUseCase(categoryId).first()
      
      if (cards.isEmpty()) {
        _uiState.update { GameState.Empty }
      } else {
        cardList = cards.shuffled()
        currentIndex = 0
        playerIndex = 0
        updateState()
      }
    }
  }

  private fun nextCard() {
    if (currentIndex < cardList.size - 1) {
      currentIndex++
      if (players.isNotEmpty()) {
        playerIndex = (playerIndex + 1) % players.size
      }
      updateState()
    } else {
      _uiState.update { GameState.Empty }
    }
  }

  private fun updateState() {
    _uiState.update {
      GameState.Success(
        currentCard = cardList[currentIndex],
        currentPlayer = if (players.isNotEmpty()) players[playerIndex] else null,
        hasMore = currentIndex < cardList.size - 1
      )
    }
  }
}
