package com.ac.drinkinggame.ui.screens.game

import android.util.Log
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
    // Configuración visual integrada para garantizar atomicidad
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

  // Estado interno de la sesión
  private var cardList = listOf<GameCard>()
  private var players = listOf<Player>()
  private var currentIndex = 0
  private var playerIndex = 0
  private var nextClickCount = 0
  
  // Cache de configuración de sesión
  private var currentSessionKey = ""
  private var currentStyleKey: String? = null
  private var isNewUiEnabled = false

  fun onIntent(intent: GameIntent) {
    when (intent) {
      is GameIntent.LoadCards -> loadCards(intent.categoryId)
      GameIntent.NextCard -> {
        nextClickCount++
        Log.d("GameDebug", "Click Siguiente #$nextClickCount | Mostrando índice: ${currentIndex + 1} de ${cardList.size}")
        nextCard()
      }
    }
  }

  private fun loadCards(categoryId: String) {
    viewModelScope.launch {
      Log.d("GameDebug", "--- Nueva Sesión de Juego: $categoryId ---")
      
      // Reinicio síncrono
      _uiState.update { GameState.Loading }
      cardList = emptyList()
      currentIndex = 0
      playerIndex = 0
      nextClickCount = 0
      currentSessionKey = UUID.randomUUID().toString()

      try {
        // 1. Cargar configuración (Atomicamente con el primer Success)
        val category = getCategoryByIdUseCase(categoryId).first()
        isNewUiEnabled = gameRepository.isFeatureFlagActive("use_new_game_ui")
        currentStyleKey = category?.styleKey

        // 2. Obtener jugadores
        players = playerRepository.getPlayers().first()

        // 3. Sincronizar y esperar
        syncCardsByCategoryUseCase(categoryId)

        // 4. Obtener cartas finales
        val cards = getCardsByCategoryUseCase(categoryId).first()
        
        if (cards.isEmpty()) {
          _uiState.update { GameState.Empty }
        } else {
          cardList = cards.shuffled()
          Log.d("GameDebug", "Juego Inicializado. Total: ${cardList.size} | Session: $currentSessionKey")
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
