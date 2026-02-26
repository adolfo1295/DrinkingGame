package com.ac.drinkinggame.ui.screens.game

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ac.drinkinggame.domain.model.GameCard
import com.ac.drinkinggame.ui.theme.FamiliarPrimary
import com.ac.drinkinggame.ui.theme.LocoPrimary
import com.ac.drinkinggame.ui.theme.NightclubCard
import com.ac.drinkinggame.ui.theme.SabiondoPrimary
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    categoryId: String,
    onBack: () -> Unit,
    viewModel: GameViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(categoryId) {
        viewModel.onIntent(GameIntent.LoadCards(categoryId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = state,
                label = "game_content"
            ) { targetState ->
                when (targetState) {
                    GameState.Loading -> LoadingView()
                    is GameState.Success -> SuccessView(
                        card = targetState.currentCard,
                        player = targetState.currentPlayer,
                        onNext = { viewModel.onIntent(GameIntent.NextCard) }
                    )
                    GameState.Empty -> EmptyView(onRestart = onBack)
                    is GameState.Error -> ErrorView(targetState.message)
                }
            }
        }
    }
}

@Composable
private fun SuccessView(
    card: GameCard, 
    player: com.ac.drinkinggame.domain.model.Player?, 
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Turno del Jugador
        player?.let {
            Card(
                colors = CardDefaults.cardColors(containerColor = SabiondoPrimary.copy(alpha = 0.15f)),
                shape = CircleShape,
                border = BorderStroke(1.dp, SabiondoPrimary.copy(alpha = 0.3f))
            ) {
                Text(
                    text = "Turno de: ${it.name}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = SabiondoPrimary,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp).testTag("player_name")
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Espacio flexible arriba

        // La Carta (Contenido Principal)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(containerColor = NightclubCard),
            shape = RoundedCornerShape(40.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val (title, color, mainText, description, penalty) = when (card) {
                    is GameCard.Trivia -> {
                        listOf("TRIVIA", SabiondoPrimary, card.question, card.options?.joinToString("\n") ?: "", card.penalty)
                    }
                    is GameCard.Challenge -> {
                        listOf("RETO", LocoPrimary, card.title, card.description, card.penalty)
                    }
                    is GameCard.Rule -> {
                        listOf("REGLA", FamiliarPrimary, card.title, card.rule, 0)
                    }
                }.let { list -> 
                    listOf(list[0] as String, list[1] as Color, list[2] as String, list[3] as String, list[4] as Int)
                }

                // Badge Pill
                Surface(
                    color = color as Color,
                    shape = CircleShape
                ) {
                    Text(
                        text = title as String,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
                        color = Color.Black,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = mainText as String,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = description as String,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.7f),
                    lineHeight = 28.sp
                )

                if ((penalty as Int) > 0) {
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = "$penalty",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black,
                        color = color
                    )
                    Text(
                        text = "TRAGOS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Black,
                        color = color.copy(alpha = 0.7f),
                        letterSpacing = 4.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1.2f)) // Espacio flexible abajo (un poco más pesado para centrado óptico)

        // Botón de Acción
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .testTag("next_card_button"),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("¡ENTENDIDO!", fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun LoadingView() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(color = SabiondoPrimary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Cargando cartas...", color = Color.White)
    }
}

@Composable
private fun EmptyView(onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🎊", fontSize = 120.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "¡FIN DE LA PARTIDA!",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            "Han sobrevivido una noche más.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("VOLVER AL INICIO", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ErrorView(message: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Ocurrió un error", color = MaterialTheme.colorScheme.error)
        Text(message, color = Color.White, textAlign = TextAlign.Center)
    }
}
