package com.ac.drinkinggame.ui.screens.game

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.ac.drinkinggame.domain.model.GameCard
import com.ac.drinkinggame.domain.model.Player
import com.ac.drinkinggame.domain.repository.PlayerRepository
import com.ac.drinkinggame.domain.usecase.GetCardsByCategoryUseCase
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

  private val getCardsByCategoryUseCase: GetCardsByCategoryUseCase = mockk()
  private val syncCardsByCategoryUseCase: SyncCardsByCategoryUseCase = mockk()
  private val playerRepository: PlayerRepository = mockk()

  @Before
  fun setup() {
    stopKoin()
    startKoin {
      modules(module {
        single { getCardsByCategoryUseCase }
        single { syncCardsByCategoryUseCase }
        single { playerRepository }
        viewModelOf(::GameViewModel)
      })
    }
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun gameScreen_loadsAndDisplaysFirstCardCorrectly() {
    val mockPlayer = Player("1", "Adolfo")
    val mockCards = listOf(
      GameCard.Challenge("id1", "cat1", "Hacer 10 lagartijas", "Si no, bebes", 3)
    )

    every { playerRepository.getPlayers() } returns flowOf(listOf(mockPlayer))
    every { getCardsByCategoryUseCase("cat1") } returns flowOf(mockCards)
    coEvery { syncCardsByCategoryUseCase("cat1") } returns Result.success(Unit)

    composeTestRule.setContent {
      DrinkingGameTheme {
        GameScreen(categoryId = "cat1", onBack = {})
      }
    }

    composeTestRule.onNodeWithTag("player_name").assertTextContains("Adolfo", substring = true)
    composeTestRule.onNodeWithText("Hacer 10 lagartijas").assertIsDisplayed()
    composeTestRule.onNodeWithText("3").assertIsDisplayed()
  }

  @Test
  fun gameScreen_clickNext_disablesButtonDuringAnimation() {
    val mockCards = listOf(
      GameCard.Rule("id1", "cat1", "Regla 1", "Contenido 1", null),
      GameCard.Rule("id2", "cat1", "Regla 2", "Contenido 2", null)
    )
    every { playerRepository.getPlayers() } returns flowOf(emptyList())
    every { getCardsByCategoryUseCase("cat1") } returns flowOf(mockCards)
    coEvery { syncCardsByCategoryUseCase("cat1") } returns Result.success(Unit)

    composeTestRule.setContent {
      DrinkingGameTheme {
        GameScreen(categoryId = "cat1", onBack = {})
      }
    }

    composeTestRule.mainClock.autoAdvance = false
    composeTestRule.onNodeWithTag("next_card_button").performClick()
    composeTestRule.mainClock.advanceTimeBy(100)

    composeTestRule.onNodeWithTag("next_card_button")
      .assertIsNotEnabled()
      .assertTextContains("BARAJANDO...", substring = true)

    composeTestRule.mainClock.advanceTimeBy(600)
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithTag("next_card_button")
      .assertIsEnabled()
      .assertTextContains("¡ENTENDIDO!", substring = true)
  }

  @Test
  fun gameScreen_showsEmptyViewWhenApiFailsAndNoCache() {
    every { playerRepository.getPlayers() } returns flowOf(emptyList())
    every { getCardsByCategoryUseCase("cat1") } returns flowOf(emptyList())
    coEvery { syncCardsByCategoryUseCase("cat1") } returns Result.failure(Exception("Error de red"))

    composeTestRule.setContent {
      DrinkingGameTheme {
        GameScreen(categoryId = "cat1", onBack = {})
      }
    }

    // Tras el refactor, si no hay nada en cache, mostramos la pantalla de fin/vacia
    composeTestRule.onNodeWithText("¡FIN DE LA PARTIDA!", substring = true).assertIsDisplayed()
  }
}
