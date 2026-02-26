package com.ac.drinkinggame.data.remote

import com.ac.drinkinggame.data.remote.dto.CardDto
import com.ac.drinkinggame.data.remote.dto.CategoryDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class GameApiService(private val client: HttpClient) {

  suspend fun getCategories(): Result<List<CategoryDto>> = runCatching {
    client.get("categories") {
      parameter("select", "*")
    }.body()
  }

  suspend fun getCardsByCategory(categoryId: String): Result<List<CardDto>> = runCatching {
    client.get("cards") {
      parameter("select", "*")
      parameter("category_id", "eq.$categoryId")
    }.body()
  }
}
