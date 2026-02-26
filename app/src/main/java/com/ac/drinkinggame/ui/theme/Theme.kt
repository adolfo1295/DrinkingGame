package com.ac.drinkinggame.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = SabiondoPrimary,
    secondary = LocoPrimary,
    tertiary = FamiliarPrimary,
    background = SlateDark,
    surface = CardSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    error = PenaltyRed
)

@Composable
fun DrinkingGameTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme, // Forzamos DarkColorScheme
        typography = Typography,
        content = content
    )
}
