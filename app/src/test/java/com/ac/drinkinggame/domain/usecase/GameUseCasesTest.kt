package com.ac.drinkinggame.domain.usecase

import com.ac.drinkinggame.domain.repository.GameRepository
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GameUseCasesTest {

    private val repository: GameRepository = mockk()
    private val getCategoriesUseCase = GetCategoriesUseCase(repository)

    @Test
    fun `GetCategoriesUseCase should call repository and return flow`() = runTest {
        // Given
        val mockCategories = emptyList<com.ac.drinkinggame.domain.model.Category>()
        every { repository.getCategories() } returns flowOf(mockCategories)

        // When
        val result = getCategoriesUseCase().first()

        // Then
        assertEquals(mockCategories, result)
        verify { repository.getCategories() }
    }
}
