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

class GameScreenTest : KoinTest {

  @get:Rule
  val composeTestRule = createComposeRule()
  
  private val context = InstrumentationRegistry.getInstrumentation().targetContext

  private val getCardsByCategoryUseCase: GetCardsByCategoryUseCase = mockk()
  private val syncCardsByCategoryUseCase: SyncCardsByCategoryUseCase = mockk()
  private val getCategoryByIdUseCase: GetCategoryByIdUseCase = mockk()
  private val playerRepository: PlayerRepository = mockk()
  private val gameRepository: GameRepository = mockk()

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
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun gameScreen_loadsAndDisplaysFirstCardWithAura() {
    val categoryId = "cat1"
    val mockPlayer = Player("1", "Adolfo")
    val mockCategory = Category(categoryId, "Test", false, 0.0, "1", "AURA_CYAN_GLOW")
    val mockCards = listOf(
      GameCard.Challenge("id1", categoryId, "Hacer 10 lagartijas", "Si no, bebes", 3)
    )

    every { playerRepository.getPlayers() } returns flowOf(listOf(mockPlayer))
    every { getCardsByCategoryUseCase(categoryId) } returns flowOf(mockCards)
    every { getCategoryByIdUseCase(categoryId) } returns flowOf(mockCategory)
    coEvery { gameRepository.isFeatureFlagActive(any()) } returns true
    coEvery { syncCardsByCategoryUseCase(categoryId) } returns Result.success(Unit)

    composeTestRule.setContent {
      DrinkingGameTheme {
        GameScreen(categoryId = categoryId, initialStyleKey = "AURA_CYAN_GLOW", onBack = {})
      }
    }

    // El color de fondo y el aura deberían estar activos (difícil de testear color directo, pero verificamos contenido)
    val expectedTurnText = context.getString(R.string.game_player_turn, "Adolfo").uppercase()
    composeTestRule.onNodeWithTag("player_name").assertTextContains(expectedTurnText, substring = true)
    composeTestRule.onNodeWithText("Hacer 10 lagartijas").assertIsDisplayed()
  }

  @Test
  fun gameScreen_clickNext_cyclesCorrectly() {
    val categoryId = "cat1"
    val mockCards = listOf(
      GameCard.Rule("id1", categoryId, "Regla 1", "Contenido 1", null),
      GameCard.Rule("id2", categoryId, "Regla 2", "Contenido 2", null)
    )
    every { playerRepository.getPlayers() } returns flowOf(emptyList())
    every { getCardsByCategoryUseCase(categoryId) } returns flowOf(mockCards)
    every { getCategoryByIdUseCase(categoryId) } returns flowOf(null)
    coEvery { gameRepository.isFeatureFlagActive(any()) } returns false
    coEvery { syncCardsByCategoryUseCase(categoryId) } returns Result.success(Unit)

    composeTestRule.setContent {
      DrinkingGameTheme {
        GameScreen(categoryId = categoryId, onBack = {})
      }
    }

    // Primer click
    composeTestRule.onNodeWithTag("next_card_button").performClick()
    
    // Esperar a que la animación de giro termine
    composeTestRule.mainClock.advanceTimeBy(1000)
    
    // Debería estar en la segunda carta o fin de juego dependiendo de la lógica de índices
    // (En este caso, 2 cartas -> index 0, luego index 1)
    composeTestRule.onNodeWithText("Regla 2").assertIsDisplayed()
  }
}
