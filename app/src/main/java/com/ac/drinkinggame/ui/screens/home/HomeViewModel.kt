package com.ac.drinkinggame.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.domain.model.Player
import com.ac.drinkinggame.domain.repository.PlayerRepository
import com.ac.drinkinggame.domain.usecase.GetCategoriesUseCase
import com.ac.drinkinggame.domain.usecase.SyncCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface HomeState {
  data object Loading : HomeState
  data class Success(
    val categories: List<Category>,
    val players: List<Player> = emptyList()
  ) : HomeState

  data class Error(val message: String) : HomeState
}

class HomeViewModel(
  private val getCategoriesUseCase: GetCategoriesUseCase,
  private val syncCategoriesUseCase: SyncCategoriesUseCase,
  private val playerRepository: PlayerRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow<HomeState>(HomeState.Loading)
  val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

  // Flag para saber si ya intentamos la primera sincronización
  private var isSyncCompleted = false

  init {
    loadData()
    syncData()
  }

  private fun loadData() {
    viewModelScope.launch {
      combine(
        getCategoriesUseCase(),
        playerRepository.getPlayers()
      ) { categories, players ->
        // Solo pasamos a Success si hay categorías O si la sincronización ya terminó
        // Esto evita la pantalla vacía en el primer arranque
        if (categories.isEmpty() && !isSyncCompleted) {
          HomeState.Loading
        } else {
          val sortedCategories = categories.sortedBy { it.isPremium }
          HomeState.Success(sortedCategories, players)
        }
      }.collect { state ->
        _uiState.update { state }
      }
    }
  }

  private fun syncData() {
    viewModelScope.launch {
      syncCategoriesUseCase()
        .onSuccess {
          isSyncCompleted = true
          // Al marcar isSyncCompleted, el combine de loadData reaccionará si Room seguía vacío
        }
        .onFailure { error ->
          isSyncCompleted = true
          if (_uiState.value !is HomeState.Success) {
            _uiState.update { HomeState.Error(error.message ?: "Sin conexión") }
          }
        }
    }
  }

  fun addPlayer(name: String) {
    viewModelScope.launch {
      playerRepository.addPlayer(name)
    }
  }

  fun removePlayer(playerId: String) {
    viewModelScope.launch {
      playerRepository.removePlayer(playerId)
    }
  }
}
