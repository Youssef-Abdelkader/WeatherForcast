package com.youssef.weatherforcast.Data.LocalDataSource

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.youssef.weatherforcast.Model.*
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoriteDaoTest {

    private lateinit var favoriteDao: FavoriteDao
    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        favoriteDao = db.favoriteDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testInsertAndGetAllFavorites() = runTest {
        val favoriteLocation = createTestFavorite()

        favoriteDao.insertFavorite(favoriteLocation)
        val favorites = favoriteDao.getAllFavorites().first()

        assertTrue(favorites.contains(favoriteLocation))
    }

    @Test
    fun testInsertAndGetHomeData() = runTest {
        val homeData = createTestHomeData()

        favoriteDao.insertHomeData(homeData)
        val result = favoriteDao.getHomeData().first()

        assertNotNull(result)
        assertEquals(homeData.id, result?.id)
        assertEquals(homeData.weather, result?.weather)
        assertEquals(homeData.forecast, result?.forecast)
    }

    @Test
    fun testInsertAndGetAllAlerts() = runTest {
        val weatherAlert = createTestAlert()

        favoriteDao.insertAlert(weatherAlert)
        val alerts = favoriteDao.getAllAlerts().first()

        assertTrue(alerts.contains(weatherAlert))
    }

    @Test
    fun testDeleteFavorite() = runTest {
        val favoriteLocation = createTestFavorite()

        favoriteDao.insertFavorite(favoriteLocation)
        favoriteDao.deleteFavorite(favoriteLocation)
        val favorites = favoriteDao.getAllFavorites().first()

        assertTrue(favorites.isEmpty())
    }

    @Test
    fun testDeleteAlert() = runTest {
        val weatherAlert = createTestAlert()

        favoriteDao.insertAlert(weatherAlert)
        favoriteDao.deleteAlert(weatherAlert)
        val alerts = favoriteDao.getAllAlerts().first()

        assertTrue(alerts.isEmpty())
    }

    // Helper methods
    private fun createTestFavorite() = FavoriteLocation(
        id = 1,
        locationName = "Paris",
        latitude = 48.8566,
        longitude = 2.3522,
        countryCode = "FR"
    )

    private fun createTestHomeData() = HomeData(
        id = 1,
        weather = null,
        forecast = null
    )

    private fun createTestAlert() = WeatherAlert(
        id = 1,
        type = AlertType.ALARM,
        message = "Thunderstorm warning",
        timestamp = System.currentTimeMillis(),
        startTime = "2025-04-01T10:00:00Z",
        endTime = "2025-04-01T12:00:00Z"
    )

}