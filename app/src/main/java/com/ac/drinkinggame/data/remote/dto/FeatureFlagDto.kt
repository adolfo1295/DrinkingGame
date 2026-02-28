package com.ac.drinkinggame.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeatureFlagDto(
  val id: String,
  @SerialName("is_active") val isActive: Boolean
)
