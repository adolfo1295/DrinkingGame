package com.ac.drinkinggame.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ac.drinkinggame.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
  @PrimaryKey val id: String,
  val name: String,
  val isPremium: Boolean,
  val price: Double,
  val version: String
)

fun CategoryEntity.toDomain() = Category(
  id = id,
  name = name,
  isPremium = isPremium,
  price = price,
  version = version
)

fun Category.toEntity() = CategoryEntity(
  id = id,
  name = name,
  isPremium = isPremium,
  price = price,
  version = version
)
