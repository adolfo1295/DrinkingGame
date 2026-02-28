package com.ac.drinkinggame.ui.screens.game.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.ac.drinkinggame.domain.model.GameCard
import com.ac.drinkinggame.ui.theme.NightclubCard

@Composable
fun ClassicGameCard(
  card: GameCard,
  rotation: Float,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp)
      .graphicsLayer {
        rotationY = rotation
        cameraDistance = 12f * density
      },
    colors = CardDefaults.cardColors(containerColor = NightclubCard),
    shape = RoundedCornerShape(40.dp),
    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
  ) {
    val isFlipped = rotation > 90f
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .graphicsLayer { if (isFlipped) rotationY = 180f }
    ) {
      CardContent(card = card, accentColor = accentColor, isNeon = false)
    }
  }
}
