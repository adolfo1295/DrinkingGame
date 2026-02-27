package com.ac.drinkinggame.data.repository

import com.ac.drinkinggame.data.local.dao.CardDao
import com.ac.drinkinggame.data.local.dao.CategoryDao
import com.ac.drinkinggame.data.local.entity.CategoryEntity
import com.ac.drinkinggame.data.remote.GameApiService
import com.ac.drinkinggame.data.remote.dto.CategoryDto
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRepositoryImplTest {

    private val apiService: GameApiService = mockk()
    private val categoryDao: CategoryDao = mockk()
    private val cardDao: CardDao = mockk()
    private val repository = GameRepositoryImpl(apiService, categoryDao, cardDao)

    @Test
    fun `getCategories should return flow of categories from dao`() = runTest {
        // Given
        val mockEntities = listOf(
            CategoryEntity("1", "Trivia", false, 0.0, "1.0")
        )
        every { categoryDao.getCategories() } returns flowOf(mockEntities)

        // When
        val result = repository.getCategories().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("Trivia", result[0].name)
    }

    @Test
    fun `syncCategories should fetch from api and insert into dao`() = runTest {
        // Given
        val mockDtos = listOf(
            CategoryDto("1", "Trivia", false, 0.0, "1.0")
        )
        coEvery { apiService.getCategories() } returns Result.success(mockDtos)
        coEvery { categoryDao.insertCategories(any()) } returns listOf(1L)

        // When
        val result = repository.syncCategories()

        // Then
        assertTrue(result.isSuccess)
        coVerify { apiService.getCategories() }
        coVerify { categoryDao.insertCategories(any()) }
    }
}
