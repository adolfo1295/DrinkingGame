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
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

class HomeScreenTest : KoinTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val getCategoriesUseCase: GetCategoriesUseCase = mockk()
    private val playerRepository: PlayerRepository = mockk()
    
    // StateFlow para simular la base de datos reactiva de jugadores
    private val playersFlow = MutableStateFlow<List<Player>>(emptyList())

    @Before
    fun setup() {
        stopKoin()
        startKoin {
            modules(module {
                single { getCategoriesUseCase }
                single { playerRepository }
                viewModel { HomeViewModel(get(), get()) }
            })
        }
        
        // Setup base de mocks
        coEvery { getCategoriesUseCase() } returns Result.success(
            listOf(
                Category("1", "Gratis", false, "1.0"),
                Category("2", "Premium", true, "1.0")
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

        composeTestRule.onNodeWithText("Añade al menos un jugador para empezar").assertIsDisplayed()
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

        // Abrir diálogo
        composeTestRule.onNodeWithTag("players_button").performClick()
        
        // Escribir nombre
        composeTestRule.onNodeWithTag("player_input").performTextInput("Adolfo")
        
        // Confirmar
        composeTestRule.onNodeWithTag("add_player_confirm").performClick()

        // Verificar que el nombre aparece en la lista dentro del diálogo
        composeTestRule.onNodeWithText("Adolfo").assertIsDisplayed()
    }

    @Test
    fun homeScreen_clickPremiumCategory_showsPaywall() {
        // Añadimos un jugador para habilitar los clics
        playersFlow.value = listOf(Player("1", "User"))

        composeTestRule.setContent {
            DrinkingGameTheme { HomeScreen(onCategorySelected = {}) }
        }

        // Clic en la categoría premium (la que tiene el candado)
        composeTestRule.onNodeWithText("Premium").performClick()

        // Verificar que aparece el diálogo de Paywall
        composeTestRule.onNodeWithText("🚀 ¡Pásate a Premium!").assertIsDisplayed()
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

        // Clic en categoría gratuita
        composeTestRule.onNodeWithText("Gratis").performClick()

        // Verificar que la navegación se activó con el ID correcto
        assert(navigatedCategoryId == "1")
    }
}
