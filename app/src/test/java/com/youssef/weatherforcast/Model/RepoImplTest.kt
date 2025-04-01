// RepoImplTest.kt
package com.youssef.weatherforcast.Model

import com.youssef.weatherforcast.Data.LocalDataSource.FavoriteDao
import com.youssef.weatherforcast.Data.RemoteDataSource.RemoteDataSource
import com.youssef.weatherforcast.Setting.SettingsPreferences
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.text.NumberFormat
import java.util.Locale

class RepoImplTest {
    private val mockRemoteDataSource = mockk<RemoteDataSource>(relaxed = true)
    private val mockSettingsPrefs = mockk<SettingsPreferences>(relaxed = true)
    private val mockFavoriteDao = mockk<FavoriteDao>(relaxed = true)
    private lateinit var repo: RepoImpl

    @Before
    fun setup() {
        repo = RepoImpl(mockRemoteDataSource, mockSettingsPrefs, mockFavoriteDao)
    }

    @Test
    fun insertFavorite_shouldDelegateToDAO() = runTest {
        val testFavorite = FavoriteLocation(
            id = 1,
            locationName = "Cairo",
            latitude = 30.0444,
            longitude = 31.2357,
            countryCode = "EG"
        )
        coEvery { mockFavoriteDao.insertFavorite(testFavorite) } returns Unit

        repo.insertFavorite(testFavorite)

        coVerify(exactly = 1) { mockFavoriteDao.insertFavorite(testFavorite) }
    }

    @Test
    fun deleteFavorite_shouldRemoveFromDAO() = runTest {
        val testFavorite = FavoriteLocation(
            id = 2,
            locationName = "Paris",
            latitude = 48.8566,
            longitude = 2.3522,
            countryCode = "FR"
        )
        coEvery { mockFavoriteDao.deleteFavorite(testFavorite) } returns Unit

        repo.deleteFavorite(testFavorite)

        coVerify(exactly = 1) { mockFavoriteDao.deleteFavorite(testFavorite) }
    }

    @Test
    fun insertAlert_shouldPersistViaDAO() = runTest {
        val testAlert = WeatherAlert(
            id = 1,
            type = AlertType.ALARM,
            message = "Storm Warning",
            timestamp = System.currentTimeMillis(),
            startTime = "2024-04-01T10:00:00Z",
            endTime = "2024-04-01T12:00:00Z"
        )
        coEvery { mockFavoriteDao.insertAlert(testAlert) } returns Unit

        repo.insertAlert(testAlert)

        coVerify(exactly = 1) { mockFavoriteDao.insertAlert(testAlert) }
    }

    @Test
    fun deleteAlert_shouldRemoveFromDAO() = runTest {
        val testAlert = WeatherAlert(
            id = 2,
            type = AlertType.NOTIFICATION,
            message = "Rain Alert",
            timestamp = System.currentTimeMillis(),
            startTime = "2024-04-02T08:00:00Z",
            endTime = "2024-04-02T10:00:00Z"
        )
        coEvery { mockFavoriteDao.deleteAlert(testAlert) } returns Unit

        repo.deleteAlert(testAlert)

        coVerify(exactly = 1) { mockFavoriteDao.deleteAlert(testAlert) }
    }

    @Test
    fun getLocalizedUnit_shouldReturnCorrectSymbols() {
        val testCases = listOf(
            Triple("Arabic", "C", "س"),
            Triple("English", "F", "F"),
            Triple("Arabic", "Meter/sec", "م/ث"),
            Triple("English", "Mile/hour", "mph"),
            Triple("French", "K", "K") // Default case
        )

        testCases.forEach { (lang, unit, expected) ->
            every { mockSettingsPrefs.getSetting("language", "English") } returns lang
            val result = repo.getLocalizedUnit(unit)
            assertEquals("Failed for $lang/$unit", expected, result)
        }
    }

    @Test
    fun formatNumber_shouldUseCorrectLocale() {
        every { mockSettingsPrefs.getSetting("language", "English") } returns "Arabic"
        val testValue = 1234.56

        val result = repo.formatNumber(testValue)

        val arabicFormat = NumberFormat.getInstance(Locale("ar")).format(testValue)
        assertEquals(arabicFormat, result)
    }

    @Test
    fun saveSetting_shouldDelegateToPreferences() {
        every { mockSettingsPrefs.saveSetting("temperature_unit", "C") } returns Unit

        repo.saveSetting("temperature_unit", "C")

        coVerify { mockSettingsPrefs.saveSetting("temperature_unit", "C") }
    }


    @Test
    fun insertHomeData_shouldPersistViaDAO() = runTest {
        val testHomeData = HomeData(id = 1, weather = null, forecast = null)
        coEvery { mockFavoriteDao.insertHomeData(testHomeData) } returns Unit

        repo.insertHomeData(testHomeData)  // ✅ FIXED function name

        coVerify(exactly = 1) { mockFavoriteDao.insertHomeData(testHomeData) }
    }
}
