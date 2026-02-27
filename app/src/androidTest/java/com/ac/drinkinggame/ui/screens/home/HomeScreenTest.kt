package com.ac.drinkinggame.ui.screens.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.domain.model.Player
import com.ac.drinkinggame.domain.repository.PlayerRepository
import com.ac.drinkinggame.domain.usecase.GetCategoriesUseCase
import com.ac.drinkinggame.ui.theme.DrinkingGameTheme
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

class HomeScreenTest : KoinTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val getCategoriesUseCase: GetCategoriesUseCase = mockk()
    private val playerRepository: PlayerRepository = mockk()
    
    private val playersFlow = MutableStateFlow<List<Player>>(emptyList())

    @Before
    fun setup() {
        stopKoin()
        startKoin {
            modules(module {
                single { getCategoriesUseCase }
                single { playerRepository }
                viewModelOf(::HomeViewModel)
            })
        }
        
        coEvery { getCategoriesUseCase() } returns Result.success(
            listOf(
                Category("1", "Gratis", false, 0.0, "1.0"),
                Category("2", "Premium", true, 9.99, "1.0")
            )
        )
        every { playerRepository.getPlayers() } returns playersFlow
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun homeScreen_noPlayers_showsWarningMessage() {
        composeTestRule.setContent {
            DrinkingGameTheme { HomeScreen(onCategorySelected = {}) }
        }

        // El texto cambió sutilmente en el refactor
        composeTestRule.onNodeWithText("Se requiere al menos un jugador para iniciar el juego.", substring = true).assertIsDisplayed()
    }

    @Test
    fun homeScreen_addPlayerFlow_updatesUI() {
        coEvery { playerRepository.addPlayer(any()) } answers {
            val name = firstArg<String>()
            playersFlow.value = playersFlow.value + Player("id", name)
        }

        composeTestRule.setContent {
            DrinkingGameTheme { HomeScreen(onCategorySelected = {}) }
        }

        composeTestRule.onNodeWithTag("players_button").performClick()
        composeTestRule.onNodeWithTag("player_input").performTextInput("Adolfo")
        composeTestRule.onNodeWithTag("add_player_confirm").performClick()

        composeTestRule.onNodeWithText("Adolfo").assertIsDisplayed()
    }

    @Test
    fun homeScreen_clickPremiumCategory_showsPaywall() {
        playersFlow.value = listOf(Player("1", "User"))

        composeTestRule.setContent {
            DrinkingGameTheme { HomeScreen(onCategorySelected = {}) }
        }

        composeTestRule.onNodeWithText("Premium").performClick()
        composeTestRule.onNodeWithText("🚧 ¡Próximamente!").assertIsDisplayed()
    }

    @Test
    fun homeScreen_clickFreeCategory_triggersNavigation() {
        var navigatedCategoryId: String? = null
        playersFlow.value = listOf(Player("1", "User"))

        composeTestRule.setContent {
            DrinkingGameTheme {
                HomeScreen(onCategorySelected = { navigatedCategoryId = it })
            }
        }

        composeTestRule.onNodeWithText("Gratis").performClick()
        assert(navigatedCategoryId == "1")
    }
}
