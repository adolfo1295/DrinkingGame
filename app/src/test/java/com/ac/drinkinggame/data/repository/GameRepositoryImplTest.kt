package com.ac.drinkinggame.data.repository

import com.ac.drinkinggame.data.remote.GameApiService
import com.ac.drinkinggame.data.remote.dto.CategoryDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRepositoryImplTest {

    private val apiService: GameApiService = mockk()
    private val repository = GameRepositoryImpl(apiService)

    @Test
    fun `getCategories should return success when api call is successful`() = runTest {
        // Given
        val mockDtos = listOf(
            CategoryDto("1", "Trivia", false, 0.0, "1.0")
        )
        coEvery { apiService.getCategories() } returns Result.success(mockDtos)

        // When
        val result = repository.getCategories()

        // Then
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Trivia", result.getOrNull()?.get(0)?.name)
    }

    @Test
    fun `getCategories should return failure when api call fails`() = runTest {
        // Given
        val exception = Exception("Network error")
        coEvery { apiService.getCategories() } returns Result.failure(exception)

        // When
        val result = repository.getCategories()

        // Then
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
