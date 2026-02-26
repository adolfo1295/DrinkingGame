package com.ac.drinkinggame.ui.screens.game

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.ac.drinkinggame.domain.model.GameCard
import com.ac.drinkinggame.domain.model.Player
import com.ac.drinkinggame.domain.repository.PlayerRepository
import com.ac.drinkinggame.domain.usecase.GetCardsByCategoryUseCase
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
  private val playerRepository: PlayerRepository = mockk()

  @Before
  fun setup() {
    stopKoin()
    startKoin {
      modules(module {
        single { getCardsByCategoryUseCase }
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
    coEvery { getCardsByCategoryUseCase("cat1") } returns Result.success(mockCards)

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
    // Given: Dos cartas para que haya un cambio real
    val mockCards = listOf(
      GameCard.Rule("id1", "cat1", "Regla 1", "Contenido 1", null),
      GameCard.Rule("id2", "cat1", "Regla 2", "Contenido 2", null)
    )
    every { playerRepository.getPlayers() } returns flowOf(emptyList())
    coEvery { getCardsByCategoryUseCase("cat1") } returns Result.success(mockCards)

    composeTestRule.setContent {
      DrinkingGameTheme {
        GameScreen(categoryId = "cat1", onBack = {})
      }
    }

    // Detenemos el reloj automático para controlar la animación manualmente
    composeTestRule.mainClock.autoAdvance = false

    // When: Hacemos clic en el botón
    composeTestRule.onNodeWithTag("next_card_button").performClick()

    // Avanzamos un poco de tiempo (ej. 100ms de los 500ms que dura el giro)
    composeTestRule.mainClock.advanceTimeBy(100)

    // Then: El botón debe estar deshabilitado y mostrando el texto de carga
    composeTestRule.onNodeWithTag("next_card_button")
      .assertIsNotEnabled()
      .assertTextContains("BARAJANDO...", substring = true)

    // Avanzamos hasta el final de la animación (más de 500ms)
    composeTestRule.mainClock.advanceTimeBy(600)
    composeTestRule.waitForIdle()

    // Then: El botón debe habilitarse de nuevo
    composeTestRule.onNodeWithTag("next_card_button")
      .assertIsEnabled()
      .assertTextContains("¡ENTENDIDO!", substring = true)
  }

  @Test
  fun gameScreen_showsErrorViewWhenApiFails() {
    every { playerRepository.getPlayers() } returns flowOf(emptyList())
    coEvery { getCardsByCategoryUseCase("cat1") } returns Result.failure(Exception("Error de red"))

    composeTestRule.setContent {
      DrinkingGameTheme {
        GameScreen(categoryId = "cat1", onBack = {})
      }
    }

    composeTestRule.onNodeWithText("Ocurrió un error").assertIsDisplayed()
    composeTestRule.onNodeWithText("Error de red").assertIsDisplayed()
  }
}
