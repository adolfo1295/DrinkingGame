package com.ac.drinkinggame.domain.model

import androidx.compose.ui.graphics.Color
import com.ac.drinkinggame.ui.theme.FamiliarPrimary
import com.ac.drinkinggame.ui.theme.LocoPrimary
import com.ac.drinkinggame.ui.theme.SabiondoPrimary
import com.ac.drinkinggame.ui.theme.SuccessGreen

enum class Category(
    val title: String,
    val icon: String,
    val color: Color
) {
    TRIVIA("Sabiondo", "❓", SabiondoPrimary),
    CHALLENGES("Modo Loco", "🔥", LocoPrimary),
    FAMILIAR("Familiar", "🤝", FamiliarPrimary),
    MIXED("Mixto", "🎲", SuccessGreen)
}
