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
  val price: Double,
  val version: String
)

fun CategoryEntity.toDomain(isEnglish: Boolean = false) = Category(
  id = id,
  name = if (isEnglish && nameEn != null) nameEn else name,
  isPremium = isPremium,
  price = price,
  version = version
)

fun Category.toEntity() = CategoryEntity(
  id = id,
  name = name, // Aquí guardamos el nombre original/español
  nameEn = null, // Nota: el Mapper de DTO se encargará de llenar esto
  isPremium = isPremium,
  price = price,
  version = version
)
