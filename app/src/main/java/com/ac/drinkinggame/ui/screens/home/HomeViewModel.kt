package com.ac.drinkinggame.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.domain.model.Player
import com.ac.drinkinggame.domain.repository.PlayerRepository
import com.ac.drinkinggame.domain.usecase.GetCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
  private val playerRepository: PlayerRepository
) : ViewModel() {

  private val _uiState = MutableStateFlow<HomeState>(HomeState.Loading)
  val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

  init {
    loadData()
  }

  private fun loadData() {
    viewModelScope.launch {
      _uiState.update { HomeState.Loading }

      // Combinamos la carga de categorías con la escucha reactiva de jugadores
      playerRepository.getPlayers().collect { players ->
        getCategoriesUseCase()
          .onSuccess { categories ->
            _uiState.update { HomeState.Success(categories, players) }
          }
          .onFailure { error ->
            _uiState.update { HomeState.Error(error.message ?: "Error al cargar datos") }
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
