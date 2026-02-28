package com.ac.drinkinggame.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ac.drinkinggame.data.local.dao.CardDao
import com.ac.drinkinggame.data.local.dao.CategoryDao
import com.ac.drinkinggame.data.local.entity.CardEntity
import com.ac.drinkinggame.data.local.entity.CategoryEntity

@Database(
  entities = [CategoryEntity::class, CardEntity::class],
  version = 3,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun categoryDao(): CategoryDao
  abstract fun cardDao(): CardDao
}
