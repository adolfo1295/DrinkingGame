package com.ac.drinkinggame.ui.screens.game

import app.cash.turbine.test
import com.ac.drinkinggame.domain.model.GameCard
import com.ac.drinkinggame.domain.repository.PlayerRepository
import com.ac.drinkinggame.domain.usecase.GetCardsByCategoryUseCase
import com.ac.drinkinggame.domain.usecase.SyncCardsByCategoryUseCase
import com.ac.drinkinggame.util.MainDispatcherRule
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class GameViewModelTest {

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  private val getCardsByCategoryUseCase: GetCardsByCategoryUseCase = mockk()
  private val syncCardsByCategoryUseCase: SyncCardsByCategoryUseCase = mockk()
  private val playerRepository: PlayerRepository = mockk {
    every { getPlayers() } returns flowOf(emptyList())
  }

  @Test
  fun `LoadCards intent should sync and then load from Room`() = runTest {
    // Given
    val mockCards = listOf(
      GameCard.Rule("1", "cat1", "Title", "Rule content", null)
    )
    coEvery { syncCardsByCategoryUseCase("cat1") } returns Result.success(Unit)
    every { getCardsByCategoryUseCase("cat1") } returns flowOf(mockCards)
    
    val viewModel = GameViewModel(getCardsByCategoryUseCase, syncCardsByCategoryUseCase, playerRepository)

    // Then
    viewModel.uiState.test {
      assertEquals(GameState.Loading, awaitItem())

      // When
      viewModel.onIntent(GameIntent.LoadCards("cat1"))

      // Success Card
      val state = awaitItem()
      assertTrue(state is GameState.Success)
      assertEquals(mockCards[0], (state as GameState.Success).currentCard)
      
      // Verify sequence
      coVerifyOrder {
        syncCardsByCategoryUseCase("cat1")
        getCardsByCategoryUseCase("cat1")
      }
    }
  }

  @Test
  fun `NextCard intent should update to next card or Empty`() = runTest {
    // Given
    val mockCards = listOf(
      GameCard.Rule("1", "cat1", "Title 1", "Rule 1", null),
      GameCard.Rule("2", "cat1", "Title 2", "Rule 2", null)
    )
    coEvery { syncCardsByCategoryUseCase("cat1") } returns Result.success(Unit)
    every { getCardsByCategoryUseCase("cat1") } returns flowOf(mockCards)
    
    val viewModel = GameViewModel(getCardsByCategoryUseCase, syncCardsByCategoryUseCase, playerRepository)

    viewModel.uiState.test {
      assertEquals(GameState.Loading, awaitItem())

      // When
      viewModel.onIntent(GameIntent.LoadCards("cat1"))

      // Success Card
      val firstState = awaitItem()
      assertTrue(firstState is GameState.Success)
      val firstCard = (firstState as GameState.Success).currentCard
      
      // When
      viewModel.onIntent(GameIntent.NextCard)
      
      // Then Success next card
      val secondState = awaitItem()
      assertTrue(secondState is GameState.Success)
      val secondCard = (secondState as GameState.Success).currentCard
      assertTrue(firstCard != secondCard)
      
      // When next again
      viewModel.onIntent(GameIntent.NextCard)
      assertEquals(GameState.Empty, awaitItem())
    }
  }
}
