package com.youssef.weatherforcast.Favourite

import com.youssef.weatherforcast.Model.FavoriteLocation
import com.youssef.weatherforcast.Model.Repo
import com.youssef.weatherforcast.Model.WeatherResponse
import com.youssef.weatherforcast.Model.ForecastResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
class FavoriteViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val mockRepo = mockk<Repo>(relaxed = true) // Prevents unnecessary stubbing errors
    private lateinit var viewModel: FavoriteViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = FavoriteViewModel(mockRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadFavorites_shouldUpdateFavoritesList() = runTest {
        // Arrange
        val testFavorites = listOf(
            FavoriteLocation(1, "Paris", 48.8566, 2.3522, "FR"),
            FavoriteLocation(2, "Cairo", 30.0444, 31.2357, "EG")
        )
        coEvery { mockRepo.getAllFavorites() } returns flowOf(testFavorites)

        // Act
        viewModel.loadFavorites()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertEquals(2, viewModel.favorites.value.size)
        assertTrue(viewModel.favorites.value.containsAll(testFavorites))
    }

    @Test
    fun removeFavorite_shouldDeleteFromRepository() = runTest {
        val testFavorite = FavoriteLocation(3, "London", 51.5074, -0.1278, "UK")
        coEvery { mockRepo.deleteFavorite(testFavorite) } returns Unit

        viewModel.removeFavorite(testFavorite)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { mockRepo.deleteFavorite(testFavorite) }
    }

    }

