package com.ac.drinkinggame.domain.usecase

import com.ac.drinkinggame.domain.repository.GameRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class GameUseCasesTest {

    private val repository: GameRepository = mockk()
    private val getCategoriesUseCase = GetCategoriesUseCase(repository)

    @Test
    fun `GetCategoriesUseCase should call repository`() = runTest {
        // Given
        coEvery { repository.getCategories() } returns Result.success(emptyList())

        // When
        val result = getCategoriesUseCase()

        // Then
        assertTrue(result.isSuccess)
    }
}
