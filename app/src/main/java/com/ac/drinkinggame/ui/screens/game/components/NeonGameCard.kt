package com.ac.drinkinggame.ui.screens.game.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.ac.drinkinggame.domain.model.GameCard

@Composable
fun NeonGameCard(
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
        cameraDistance = 15f * density
      },
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2230)),
    shape = RoundedCornerShape(32.dp),
    border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
  ) {
    val isFlipped = rotation > 90f
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .graphicsLayer { if (isFlipped) rotationY = 180f }
    ) {
      CardContent(card = card, accentColor = accentColor, isNeon = true)
    }
  }
}
