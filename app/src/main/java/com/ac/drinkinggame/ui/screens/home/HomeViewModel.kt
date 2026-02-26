package com.ac.drinkinggame.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.domain.usecase.GetCategoriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface HomeState {
    data object Loading : HomeState
    data class Success(val categories: List<Category>) : HomeState
    data class Error(val message: String) : HomeState
}

class HomeViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeState>(HomeState.Loading)
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { HomeState.Loading }
            getCategoriesUseCase()
                .onSuccess { categories ->
                    _uiState.update { HomeState.Success(categories) }
                }
                .onFailure { error ->
                    _uiState.update { HomeState.Error(error.message ?: "Error al cargar categorías") }
                }
        }
    }
}
