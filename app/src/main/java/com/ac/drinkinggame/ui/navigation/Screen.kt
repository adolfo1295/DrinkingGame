package com.ac.drinkinggame.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey {
    @Serializable
    data object CategorySelection : Screen

    @Serializable
    data class Game(val categoryId: String) : Screen
}
