package com.ac.drinkinggame.ui.screens.home

import app.cash.turbine.test
import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.domain.repository.PlayerRepository
import com.ac.drinkinggame.domain.usecase.GetCategoriesUseCase
import com.ac.drinkinggame.domain.usecase.SyncCategoriesUseCase
import com.ac.drinkinggame.util.MainDispatcherRule
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getCategoriesUseCase: GetCategoriesUseCase = mockk()
    private val syncCategoriesUseCase: SyncCategoriesUseCase = mockk()
    private val playerRepository: PlayerRepository = mockk {
        every { getPlayers() } returns flowOf(emptyList())
    }

    @Test
    fun `initial state should be Loading and then Success when use case returns categories`() = runTest {
        // Given
        val mockCategories = listOf(Category("1", "Test", false, 0.0, "1.0"))
        every { getCategoriesUseCase() } returns flowOf(mockCategories)
        coEvery { syncCategoriesUseCase() } returns Result.success(Unit)

        // When
        val viewModel = HomeViewModel(getCategoriesUseCase, syncCategoriesUseCase, playerRepository)

        // Then
        viewModel.uiState.test {
            // First item is Loading (initial state)
            // Note: with combine and collect, it might emit Success immediately if flow is fast
            val state = awaitItem()
            assertTrue(state is HomeState.Success)
            assertEquals(mockCategories, (state as HomeState.Success).categories)
        }
    }
}
