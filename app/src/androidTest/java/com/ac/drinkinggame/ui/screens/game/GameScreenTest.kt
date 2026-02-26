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
        // Given
        val mockPlayer = Player("1", "Adolfo")
        val mockCards = listOf(
            GameCard.Challenge("id1", "cat1", "Hacer 10 lagartijas", "Si no, bebes", 3)
        )
        
        every { playerRepository.getPlayers() } returns flowOf(listOf(mockPlayer))
        coEvery { getCardsByCategoryUseCase("cat1") } returns Result.success(mockCards)

        // When
        composeTestRule.setContent {
            DrinkingGameTheme {
                GameScreen(categoryId = "cat1", onBack = {})
            }
        }

        // Then
        composeTestRule.onNodeWithTag("player_name").assertTextContains("Adolfo", substring = true)
        composeTestRule.onNodeWithText("Hacer 10 lagartijas").assertIsDisplayed()
        composeTestRule.onNodeWithText("3").assertIsDisplayed()
    }

    @Test
    fun gameScreen_clickNext_showsEmptyScreenWhenNoMoreCards() {
        // Given
        val mockCards = listOf(
            GameCard.Rule("id1", "cat1", "Regla 1", "Contenido", null)
        )
        every { playerRepository.getPlayers() } returns flowOf(emptyList())
        coEvery { getCardsByCategoryUseCase("cat1") } returns Result.success(mockCards)

        composeTestRule.setContent {
            DrinkingGameTheme {
                GameScreen(categoryId = "cat1", onBack = {})
            }
        }

        composeTestRule.onNodeWithTag("next_card_button").performClick()
        composeTestRule.onNodeWithText("¡FIN DE LA PARTIDA!", substring = true).assertIsDisplayed()
    }

    @Test
    fun gameScreen_showsErrorViewWhenApiFails() {
        // Given
        every { playerRepository.getPlayers() } returns flowOf(emptyList())
        coEvery { getCardsByCategoryUseCase("cat1") } returns Result.failure(Exception("Error de red"))

        // When
        composeTestRule.setContent {
            DrinkingGameTheme {
                GameScreen(categoryId = "cat1", onBack = {})
            }
        }

        // Then
        composeTestRule.onNodeWithText("Ocurrió un error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Error de red").assertIsDisplayed()
    }
}
