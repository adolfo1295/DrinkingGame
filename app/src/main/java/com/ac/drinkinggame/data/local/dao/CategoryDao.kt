package com.ac.drinkinggame.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ac.drinkinggame.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
  @Query("SELECT * FROM categories")
  fun getCategories(): Flow<List<CategoryEntity>>

  @Query("SELECT * FROM categories WHERE id = :categoryId")
  fun getCategoryById(categoryId: String): Flow<CategoryEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategories(categories: List<CategoryEntity>): List<Long>

  @Query("DELETE FROM categories")
  suspend fun clearCategories(): Int
}
