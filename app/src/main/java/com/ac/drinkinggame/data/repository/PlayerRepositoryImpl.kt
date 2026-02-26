package com.ac.drinkinggame.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ac.drinkinggame.domain.model.Player
import com.ac.drinkinggame.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class PlayerRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : PlayerRepository {

    private val playersKey = stringPreferencesKey("players_list")

    override fun getPlayers(): Flow<List<Player>> = dataStore.data.map { preferences ->
        val playersJson = preferences[playersKey] ?: "[]"
        Json.decodeFromString(playersJson)
    }

    override suspend fun savePlayers(players: List<Player>) {
        dataStore.edit { preferences ->
            preferences[playersKey] = Json.encodeToString(players)
        }
    }

    override suspend fun addPlayer(name: String) {
        dataStore.edit { preferences ->
            val currentPlayers = Json.decodeFromString<List<Player>>(preferences[playersKey] ?: "[]").toMutableList()
            currentPlayers.add(Player(UUID.randomUUID().toString(), name))
            preferences[playersKey] = Json.encodeToString(currentPlayers)
        }
    }

    override suspend fun removePlayer(playerId: String) {
        dataStore.edit { preferences ->
            val currentPlayers = Json.decodeFromString<List<Player>>(preferences[playersKey] ?: "[]").toMutableList()
            currentPlayers.removeAll { it.id == playerId }
            preferences[playersKey] = Json.encodeToString(currentPlayers)
        }
    }
}
