package com.ac.drinkinggame.ui.screens.game

import app.cash.turbine.test
import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.domain.model.GameCard
import com.ac.drinkinggame.domain.repository.GameRepository
import com.ac.drinkinggame.domain.repository.PlayerRepository
import com.ac.drinkinggame.domain.usecase.GetCardsByCategoryUseCase
import com.ac.drinkinggame.domain.usecase.GetCategoryByIdUseCase
import com.ac.drinkinggame.domain.usecase.SyncCardsByCategoryUseCase
import com.ac.drinkinggame.util.MainDispatcherRule
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GameViewModelTest {

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  private val getCardsByCategoryUseCase: GetCardsByCategoryUseCase = mockk()
  private val syncCardsByCategoryUseCase: SyncCardsByCategoryUseCase = mockk()
  private val getCategoryByIdUseCase: GetCategoryByIdUseCase = mockk()
  private val playerRepository: PlayerRepository = mockk()
  private val gameRepository: GameRepository = mockk()

  @Before
  fun setupMocks() {
    every { playerRepository.getPlayers() } returns flowOf(emptyList())
    coEvery { gameRepository.isFeatureFlagActive(any()) } returns false
    coEvery { syncCardsByCategoryUseCase(any()) } returns Result.success(Unit)
  }

  private fun createViewModel() = GameViewModel(
    getCardsByCategoryUseCase,
    syncCardsByCategoryUseCase,
    getCategoryByIdUseCase,
    playerRepository,
    gameRepository
  )

  @Test
  fun `LoadCards intent should sync and then load cards`() = runTest {
    val categoryId = "cat1"
    val mockCategory = Category(categoryId, "Test", false, 0.0, "1", "AURA_NEON_PURPLE")
    val mockCards = listOf(GameCard.Rule("1", categoryId, "Title", "Rule content", null))
    
    every { getCategoryByIdUseCase(categoryId) } returns flowOf(mockCategory)
    every { getCardsByCategoryUseCase(categoryId) } returns flowOf(mockCards)
    
    val viewModel = createViewModel()

    viewModel.uiState.test {
      assertTrue(awaitItem() is GameState.Loading)
      viewModel.onIntent(GameIntent.LoadCards(categoryId))
      
      var state = awaitItem()
      while (state !is GameState.Success) { state = awaitItem() }
      
      assertEquals(mockCards[0], (state as GameState.Success).currentCard)
    }
  }

  @Test
  fun `Consecutive LoadCards should have different session keys`() = runTest {
    val categoryId = "cat1"
    val mockCategory = Category(categoryId, "Test", false, 0.0, "1", "AURA_CYAN_GLOW")
    val mockCards = listOf(GameCard.Rule("1", categoryId, "T", "C", null))
    
    every { getCategoryByIdUseCase(categoryId) } returns flowOf(mockCategory)
    every { getCardsByCategoryUseCase(categoryId) } returns flowOf(mockCards)
    
    val viewModel = createViewModel()

    viewModel.uiState.test {
      awaitItem() // Loading inicial
      
      // Primera Carga
      viewModel.onIntent(GameIntent.LoadCards(categoryId))
      var state1 = awaitItem()
      while (state1 !is GameState.Success) { state1 = awaitItem() }
      val session1 = (state1 as GameState.Success).sessionKey

      // Segunda Carga
      viewModel.onIntent(GameIntent.LoadCards(categoryId))
      var state2 = awaitItem()
      while (state2 !is GameState.Success) { state2 = awaitItem() }
      val session2 = (state2 as GameState.Success).sessionKey

      assertNotEquals("Session keys must be unique", session1, session2)
    }
  }
}
