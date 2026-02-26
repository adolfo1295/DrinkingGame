package com.ac.drinkinggame.domain.repository

import com.ac.drinkinggame.domain.model.Player
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun getPlayers(): Flow<List<Player>>
    suspend fun savePlayers(players: List<Player>)
    suspend fun addPlayer(name: String)
    suspend fun removePlayer(playerId: String)
}
