package com.ac.drinkinggame.ui.screens.home

import app.cash.turbine.test
import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.domain.repository.PlayerRepository
import com.ac.drinkinggame.domain.usecase.GetCategoriesUseCase
import com.ac.drinkinggame.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getCategoriesUseCase: GetCategoriesUseCase = mockk()
    private val playerRepository: PlayerRepository = mockk {
        every { getPlayers() } returns flowOf(emptyList())
    }

    @Test
    fun `initial state should be Loading and then Success when use case returns categories`() = runTest {
        // Given
        val mockCategories = listOf(Category("1", "Test", false, 0.0, "1.0"))
        coEvery { getCategoriesUseCase() } returns Result.success(mockCategories)

        // When
        val viewModel = HomeViewModel(getCategoriesUseCase, playerRepository)

        // Then
        viewModel.uiState.test {
            assertEquals(HomeState.Success(mockCategories, emptyList()), awaitItem())
        }
    }

    @Test
    fun `initial state should be Error when use case fails`() = runTest {
        // Given
        coEvery { getCategoriesUseCase() } returns Result.failure(Exception("Error context"))

        // When
        val viewModel = HomeViewModel(getCategoriesUseCase, playerRepository)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assert(state is HomeState.Error)
            assertEquals("Error context", (state as HomeState.Error).message)
        }
    }
}
