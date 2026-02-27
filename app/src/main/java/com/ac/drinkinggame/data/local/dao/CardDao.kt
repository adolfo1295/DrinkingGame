package com.ac.drinkinggame.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ac.drinkinggame.data.local.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
  @Query("SELECT * FROM cards WHERE categoryId = :categoryId")
  fun getCardsByCategory(categoryId: String): Flow<List<CardEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCards(cards: List<CardEntity>): List<Long>

  @Query("DELETE FROM cards WHERE categoryId = :categoryId")
  suspend fun clearCardsByCategory(categoryId: String): Int
}
