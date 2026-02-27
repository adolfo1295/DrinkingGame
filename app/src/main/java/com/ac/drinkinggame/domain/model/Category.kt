package com.ac.drinkinggame.domain.model

data class Category(
  val id: String,
  val name: String,
  val isPremium: Boolean,
  val price: Double,
  val version: String
)
