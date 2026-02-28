package com.ac.drinkinggame.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ac.drinkinggame.domain.model.GameCard
import com.ac.drinkinggame.domain.model.Player
import com.ac.drinkinggame.domain.repository.PlayerRepository
import com.ac.drinkinggame.domain.repository.GameRepository
import com.ac.drinkinggame.domain.usecase.GetCardsByCategoryUseCase
import com.ac.drinkinggame.domain.usecase.GetCategoryByIdUseCase
import com.ac.drinkinggame.domain.usecase.SyncCardsByCategoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface GameState {
  data object Loading : GameState
  data class Success(
    val currentCard: GameCard,
    val currentPlayer: Player?,
    val hasMore: Boolean,
    val sessionKey: String,
    val styleKey: String?,
    val useNewGameUi: Boolean
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
  private val getCategoryByIdUseCase: GetCategoryByIdUseCase,
  private val playerRepository: PlayerRepository,
  private val gameRepository: GameRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow<GameState>(GameState.Loading)
  val uiState: StateFlow<GameState> = _uiState.asStateFlow()

  private var cardList = listOf<GameCard>()
  private var players = listOf<Player>()
  private var currentIndex = 0
  private var playerIndex = 0
  
  private var currentSessionKey = ""
  private var currentStyleKey: String? = null
  private var isNewUiEnabled = false

  fun onIntent(intent: GameIntent) {
    when (intent) {
      is GameIntent.LoadCards -> loadCards(intent.categoryId)
      GameIntent.NextCard -> nextCard()
    }
  }

  private fun loadCards(categoryId: String) {
    viewModelScope.launch {
      // Reinicio síncrono para pureza de sesión
      _uiState.update { GameState.Loading }
      cardList = emptyList()
      currentIndex = 0
      playerIndex = 0
      currentSessionKey = UUID.randomUUID().toString()

      try {
        // 1. Obtener configuración de sesión (priorizando local)
        val category = getCategoryByIdUseCase(categoryId).first()
        isNewUiEnabled = gameRepository.isFeatureFlagActive("use_new_game_ui")
        currentStyleKey = category?.styleKey

        // 2. Obtener jugadores
        players = playerRepository.getPlayers().first()

        // 3. Sincronizar y esperar para integridad de datos
        syncCardsByCategoryUseCase(categoryId)

        // 4. Obtener cartas finales tras sincronización
        val cards = getCardsByCategoryUseCase(categoryId).first()
        
        if (cards.isEmpty()) {
          _uiState.update { GameState.Empty }
        } else {
          cardList = cards.shuffled()
          updateState()
        }
      } catch (e: Exception) {
        _uiState.update { GameState.Error(e.message ?: "Error desconocido") }
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
    if (cardList.isNotEmpty() && currentIndex < cardList.size) {
      _uiState.update {
        GameState.Success(
          currentCard = cardList[currentIndex],
          currentPlayer = if (players.isNotEmpty()) players[playerIndex] else null,
          hasMore = currentIndex < cardList.size - 1,
          sessionKey = currentSessionKey,
          styleKey = currentStyleKey,
          useNewGameUi = isNewUiEnabled
        )
      }
    }
  }
}
