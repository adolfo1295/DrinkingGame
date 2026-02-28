package com.ac.drinkinggame.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ac.drinkinggame.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
  @PrimaryKey val id: String,
  val name: String,
  val nameEn: String? = null,
  val isPremium: Boolean,
  val styleKey: String? = null,
  val price: Double,
  val version: String
)

fun CategoryEntity.toDomain(isEnglish: Boolean = false) = Category(
  id = id,
  name = if (isEnglish && nameEn != null) nameEn else name,
  isPremium = isPremium,
  price = price,
  version = version,
  styleKey = styleKey
)

fun Category.toEntity() = CategoryEntity(
  id = id,
  name = name,
  nameEn = null,
  isPremium = isPremium,
  price = price,
  version = version,
  styleKey = styleKey
)
