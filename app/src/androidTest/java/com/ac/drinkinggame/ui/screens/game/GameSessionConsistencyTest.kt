package com.ac.drinkinggame.ui.screens.game

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.ac.drinkinggame.R
import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.domain.model.GameCard
import com.ac.drinkinggame.domain.model.Player
import com.ac.drinkinggame.domain.repository.GameRepository
import com.ac.drinkinggame.domain.repository.PlayerRepository
import com.ac.drinkinggame.domain.usecase.GetCardsByCategoryUseCase
import com.ac.drinkinggame.domain.usecase.GetCategoryByIdUseCase
import com.ac.drinkinggame.domain.usecase.SyncCardsByCategoryUseCase
import com.ac.drinkinggame.ui.theme.DrinkingGameTheme
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

class GameSessionConsistencyTest : KoinTest {

  @get:Rule
  val composeTestRule = createComposeRule()
  
  private val context = InstrumentationRegistry.getInstrumentation().targetContext

  private val getCardsByCategoryUseCase: GetCardsByCategoryUseCase = mockk()
  private val syncCardsByCategoryUseCase: SyncCardsByCategoryUseCase = mockk()
  private val getCategoryByIdUseCase: GetCategoryByIdUseCase = mockk()
  private val playerRepository: PlayerRepository = mockk()
  private val gameRepository: GameRepository = mockk()

  private val mockCards = List(20) { i ->
    GameCard.Challenge("id$i", "cat1", "Carta $i", "Desc $i", 1)
  }

  @Before
  fun setup() {
    stopKoin()
    startKoin {
      modules(module {
        single { getCardsByCategoryUseCase }
        single { syncCardsByCategoryUseCase }
        single { getCategoryByIdUseCase }
        single { playerRepository }
        single { gameRepository }
        viewModelOf(::GameViewModel)
      })
    }

    // Comportamiento base común
    every { playerRepository.getPlayers() } returns flowOf(listOf(Player("1", "Test Player")))
    every { getCategoryByIdUseCase("cat1") } returns flowOf(Category("cat1", "Test Cat", false, 0.0, "1", "AURA_CYAN_GLOW"))
    coEvery { gameRepository.isFeatureFlagActive("use_new_game_ui") } returns false
    coEvery { syncCardsByCategoryUseCase("cat1") } returns Result.success(Unit)
    every { getCardsByCategoryUseCase("cat1") } returns flowOf(mockCards)
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun gameSession_consistentCardCount_exactly20Cards() {
    composeTestRule.setContent {
      DrinkingGameTheme {
        GameScreen(categoryId = "cat1", onBack = {})
      }
    }

    // Ya estamos viendo la Carta 1. Necesitamos 19 clicks para ver las 20 cartas.
    repeat(19) { i ->
      // Verificar que el botón existe y hacer click
      composeTestRule.onNodeWithTag("next_card_button")
        .assertIsDisplayed()
        .performClick()
      
      // Esperar a que la animación de giro termine (500ms + buffer)
      composeTestRule.mainClock.advanceTimeBy(700)
      composeTestRule.waitForIdle()
    }

    // En este punto estamos viendo la carta 20. El siguiente click debe llevarnos a EmptyView.
    composeTestRule.onNodeWithTag("next_card_button").performClick()
    composeTestRule.mainClock.advanceTimeBy(700)
    composeTestRule.waitForIdle()

    // Verificar que aparece la pantalla de fin de juego
    val expectedEmptyTitle = context.getString(R.string.game_empty_title)
    composeTestRule.onNodeWithText(expectedEmptyTitle, substring = true).assertIsDisplayed()
  }
}
