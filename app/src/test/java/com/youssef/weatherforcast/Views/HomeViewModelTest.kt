package com.youssef.weatherforcast.Home

import com.youssef.weatherforcast.Model.ForecastResponse
import com.youssef.weatherforcast.Model.HomeData
import com.youssef.weatherforcast.Model.Repo
import com.youssef.weatherforcast.Model.WeatherResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private val mockRepo = mockk<Repo>(relaxed = true)
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(mockRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadWeatherAndForecast should fetch data and update state`() = runTest {
        val testWeather = WeatherResponse(name = "Cairo")
        val testForecast = ForecastResponse(listOf())
        val homeDataSlot = slot<HomeData>()

        coEvery { mockRepo.getWeather(any(), any(), any(), any()) } returns flowOf(testWeather)
        coEvery { mockRepo.getForecast(any(), any(), any(), any()) } returns flowOf(testForecast)
        coEvery { mockRepo.insertHomeData(capture(homeDataSlot)) } returns Unit
        every { mockRepo.getSetting("language", any()) } returns "en"
        every { mockRepo.getSetting("temperature", any()) } returns "Celsius"

        viewModel.loadWeatherAndForecast(30.0, 31.0)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(testWeather, viewModel.weather.value)
        assertEquals(testForecast, viewModel.forecast.value)
        coVerify {
            mockRepo.getWeather(30.0, 31.0, "Celsius", "en")
            mockRepo.getForecast(30.0, 31.0, "Celsius", "en")
        }
        assertEquals(testWeather, homeDataSlot.captured.weather)
        assertEquals(testForecast, homeDataSlot.captured.forecast)
    }

    @Test
    fun `getWeatherAndForecast should handle errors gracefully`() = runTest {
        val error = RuntimeException("Network error")
        coEvery { mockRepo.getWeather(any(), any(), any(), any()) } returns flow { throw error }
        coEvery { mockRepo.getForecast(any(), any(), any(), any()) } returns flow { throw error }
        every { mockRepo.getSetting(any(), any()) } returns "en"

        viewModel.getWeatherAndForecast(30.0, 31.0)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(null, viewModel.weather.value)
        assertEquals(null, viewModel.forecast.value)
        verify(exactly = 0) { mockRepo.insertHomeData(any()) }
    }

    @Test
    fun `reloadData should fetch based on location mode`() = runTest {
        every { mockRepo.getSetting("location", any()) } returns "Map"
        every { mockRepo.getSetting("language", any()) } returns "en"
        viewModel.updateManualCoordinates(30.0, 31.0)

        val testWeather = WeatherResponse(name = "Manual Location")
        coEvery { mockRepo.getWeather(any(), any(), any(), any()) } returns flowOf(testWeather)

        viewModel.reloadData()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(testWeather, viewModel.weather.value)
        coVerify { mockRepo.getWeather(30.0, 31.0, "Celsius", "en") }
    }
}
