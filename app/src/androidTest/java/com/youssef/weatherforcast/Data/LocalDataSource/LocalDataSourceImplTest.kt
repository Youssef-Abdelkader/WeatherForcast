// LocalDataSourceImplTest.kt
package com.youssef.weatherforcast.Data.LocalDataSource

import com.youssef.weatherforcast.Model.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.Before
import org.junit.Test

class LocalDataSourceImplTest {

    private lateinit var localDataSource: LocalDataSourceImpl
    private val mockFavoriteDao = mockk<FavoriteDao>(relaxed = true)

    @Before
    fun setup() {
        localDataSource = LocalDataSourceImpl(mockFavoriteDao)
    }

    @Test
    fun insertAndRetrieveHomeData_shouldSucceed() = runTest {
        val testHomeData = HomeData(id = 1, weather = null, forecast = null)
        coEvery { mockFavoriteDao.getHomeData() } returns flowOf(testHomeData)

        localDataSource.insertHomeData(testHomeData)
        val result = localDataSource.getHomeData().first()

        assertThat("Operation should succeed", result, `is`(notNullValue()))
        assertThat("Retrieved data should match inserted data", result, `is`(testHomeData))
        coVerify {
            mockFavoriteDao.insertHomeData(testHomeData)
            mockFavoriteDao.getHomeData()
        }
    }

    @Test
    fun insertAndDeleteFavoriteLocation_shouldSucceed() = runTest {
        // Arrange
        val testFavorite = FavoriteLocation(
            id = 1,
            locationName = "Paris",
            latitude = 48.8566,
            longitude = 2.3522,
            countryCode = "FR"
        )
        coEvery { mockFavoriteDao.getAllFavorites() } returns flowOf(emptyList())

        localDataSource.insertFavorite(testFavorite)
        localDataSource.deleteFavorite(testFavorite)
        val favorites = localDataSource.getAllFavorites().first()

        assertThat("Favorites list should be empty", favorites.isEmpty(), `is`(true))
        coVerify(exactly = 1) {
            mockFavoriteDao.insertFavorite(testFavorite)
            mockFavoriteDao.deleteFavorite(testFavorite)
            mockFavoriteDao.getAllFavorites()
        }
    }

    @Test
    fun insertAndRetrieveWeatherAlert_shouldSucceed() = runTest {
        val testAlert = WeatherAlert(
            id = 1,
            type = AlertType.ALARM,
            message = "Storm Warning",
            timestamp = System.currentTimeMillis(),
            startTime = "2024-04-01T10:00:00Z",
            endTime = "2024-04-01T12:00:00Z"
        )
        coEvery { mockFavoriteDao.getAllAlerts() } returns flowOf(listOf(testAlert))

        localDataSource.insertAlert(testAlert)
        val alerts = localDataSource.getAllAlerts().first()

        assertThat("Alert should exist in list", alerts.contains(testAlert), `is`(true))
        assertThat("Result should be success", alerts, `is`(notNullValue()))
    }
}
