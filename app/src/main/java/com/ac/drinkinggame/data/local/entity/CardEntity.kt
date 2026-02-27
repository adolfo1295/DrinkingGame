package com.ac.drinkinggame.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
  @PrimaryKey val id: String,
  val categoryId: String,
  val type: String,
  val contentJson: String // Guardamos el JSON serializado
)
