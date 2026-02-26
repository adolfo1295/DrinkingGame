package com.ac.drinkinggame.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ac.drinkinggame.domain.model.Category
import com.ac.drinkinggame.domain.model.Player
import com.ac.drinkinggame.ui.theme.NightclubCard
import com.ac.drinkinggame.ui.theme.NightclubSurface
import com.ac.drinkinggame.ui.theme.SabiondoPrimary
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onCategorySelected: (String) -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showPlayersDialog by remember { mutableStateOf(false) }
    var showPremiumDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Drinking Game",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-1).sp
                )

                IconButton(
                    onClick = { showPlayersDialog = true },
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        .testTag("players_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Jugadores",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Selecciona un modo de juego",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(32.dp))

            when (val s = state) {
                HomeState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SabiondoPrimary)
                }

                is HomeState.Success -> {
                    if (s.players.isEmpty()) {
                        // Alerta Elegante de Jugadores
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.9f)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(20.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    "Se requiere al menos un jugador para iniciar el juego.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(s.categories) { category ->
                            CategoryCard(
                                category = category,
                                onClick = {
                                    if (s.players.isNotEmpty()) {
                                        if (category.isPremium) showPremiumDialog = true
                                        else onCategorySelected(category.id)
                                    }
                                }
                            )
                        }
                    }

                    if (showPlayersDialog) {
                        PlayersDialog(
                            players = s.players,
                            onAddPlayer = viewModel::addPlayer,
                            onRemovePlayer = viewModel::removePlayer,
                            onDismiss = { showPlayersDialog = false }
                        )
                    }

                    if (showPremiumDialog) {
                        PremiumPaywallDialog(onDismiss = { showPremiumDialog = false })
                    }
                }

                is HomeState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(s.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun PremiumPaywallDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("🚀 ¡Pásate a Premium!") },
        text = {
            Text("Desbloquea todos los modos de juego, cartas exclusivas y juega sin límites con tus amigos.")
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Ver Planes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Ahora no")
            }
        }
    )
}

@Composable
fun PlayersDialog(
    players: List<Player>,
    onAddPlayer: (String) -> Unit,
    onRemovePlayer: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newPlayerName by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false // Permite que el diálogo use más de la pantalla
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f) // Ocupa el 92% del ancho de la pantalla
                .fillMaxHeight(0.8f), // Ocupa hasta el 80% de la altura de la pantalla
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = NightclubCard),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp)
            ) {
                Text(
                    "Participantes", 
                    style = MaterialTheme.typography.headlineMedium, // Título un poco más grande
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = newPlayerName,
                        onValueChange = { newPlayerName = it },
                        placeholder = { Text("Nombre del jugador") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("player_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedBorderColor = SabiondoPrimary
                        )
                    )

                    IconButton(
                        onClick = {
                            if (newPlayerName.isNotBlank()) {
                                onAddPlayer(newPlayerName)
                                newPlayerName = ""
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .background(SabiondoPrimary, RoundedCornerShape(16.dp))
                            .testTag("add_player_confirm")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(players) { player ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = NightclubSurface,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    player.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                                IconButton(
                                    onClick = { onRemovePlayer(player.id) },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f), CircleShape)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text("LISTO", fontWeight = FontWeight.Black, color = Color.Black)
                }
            }
        }
    }
}

@Composable
private fun CategoryCard(category: Category, onClick: () -> Unit) {
    val borderColor = if (category.isPremium) Color(0xFFFFD700) else Color.White.copy(alpha = 0.1f)
    val containerColor = if (category.isPremium) NightclubCard.copy(alpha = 0.8f) else NightclubCard

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(if (category.isPremium) 2.dp else 1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (category.isPremium) {
                // Gradiente sutil para Premium
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
