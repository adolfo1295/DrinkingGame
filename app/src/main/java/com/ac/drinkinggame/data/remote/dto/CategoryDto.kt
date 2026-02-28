package com.ac.drinkinggame.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
  val id: String,
  val name: String,
  @SerialName("name_en") val nameEn: String? = null,
  @SerialName("is_premium") val isPremium: Boolean,
  @SerialName("style_key") val styleKey: String? = null,
  val price: Double,
  val version: String
)
