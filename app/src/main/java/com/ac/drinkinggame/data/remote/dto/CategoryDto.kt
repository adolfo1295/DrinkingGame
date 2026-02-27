package com.ac.drinkinggame.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
  val id: String,
  val name: String,
  @SerialName("is_premium") val isPremium: Boolean,
  val price: Double,
  val version: String
)
