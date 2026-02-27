package com.ac.drinkinggame.data.repository

import com.ac.drinkinggame.data.local.dao.CardDao
import com.ac.drinkinggame.data.local.dao.CategoryDao
import com.ac.drinkinggame.data.remote.GameApiService
import com.ac.drinkinggame.data.remote.dto.CategoryDto
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GameRepositoryImplTest {

  private val apiService: GameApiService = mockk()
  private val categoryDao: CategoryDao = mockk()
  private val cardDao: CardDao = mockk()
  private val repository = GameRepositoryImpl(apiService, categoryDao, cardDao)

  @Test
  fun `CategoryDto toDomain should use English name when isEnglish is true`() {
    // Given
    val dto = CategoryDto("1", "Nombre ES", "Name EN", false, 0.0, "1.0")

    // When
    val domain = dto.toDomain(isEnglish = true)

    // Then
    assertEquals("Name EN", domain.name)
  }

  @Test
  fun `CategoryDto toDomain should fallback to default name when English name is null`() {
    // Given
    val dto = CategoryDto("1", "Nombre ES", null, false, 0.0, "1.0")

    // When
    val domain = dto.toDomain(isEnglish = true)

    // Then
    assertEquals("Nombre ES", domain.name)
  }
}
