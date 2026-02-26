package com.ac.drinkinggame.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.ui.theme.NightclubCard

@Composable
fun CategoryCard(category: Category, onClick: () -> Unit) {
  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()
  val scale by animateFloatAsState(if (isPressed) 0.94f else 1f, label = "card_scale")

  val borderColor = if (category.isPremium) Color(0xFFFFD700) else Color.White.copy(alpha = 0.1f)
  val containerColor = if (category.isPremium) NightclubCard.copy(alpha = 0.8f) else NightclubCard

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .height(160.dp)
      .graphicsLayer {
        scaleX = scale
        scaleY = scale
      }
      .clickable(
        interactionSource = interactionSource,
        indication = null, // Usamos nuestra propia animación de escala
        onClick = onClick
      ),
    colors = CardDefaults.cardColors(containerColor = containerColor),
    shape = RoundedCornerShape(28.dp),
    border = BorderStroke(if (category.isPremium) 2.dp else 1.dp, borderColor),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isPressed) 2.dp else 12.dp)
  ) {

    Box(modifier = Modifier.fillMaxSize()) {
      if (category.isPremium) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                listOf(Color(0xFFFFD700).copy(alpha = 0.05f), Color.Transparent)
              )
            )
        )
        Surface(
          color = Color(0xFFFFD700),
          shape = RoundedCornerShape(bottomStart = 16.dp),
          modifier = Modifier.align(Alignment.TopEnd)
        ) {
          Text(
            text = "🔒 PREMIUM",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            color = Color.Black
          )
        }
      }

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = category.name,
          color = if (category.isPremium) Color.White else Color.White.copy(alpha = 0.9f),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.ExtraBold,
          textAlign = TextAlign.Center,
          lineHeight = 24.sp
        )
      }
    }
  }
}
