package com.youssef.weatherforcast.Home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youssef.weatherforcast.Model.ForecastResponse
import com.youssef.weatherforcast.Model.Repo
import com.youssef.weatherforcast.Model.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private var repository: Repo) : ViewModel() {
    private var _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather = _weather.asStateFlow()

    private var _forecast = MutableStateFlow<ForecastResponse?>(null)
    val forecast = _forecast.asStateFlow()

    private var _language = MutableStateFlow(repository.getSetting("language", "en"))
    val language = _language.asStateFlow()

    private var _units = MutableStateFlow(repository.getSetting("temperature", "Celsius"))
    val units = _units.asStateFlow()

    private var _location = MutableStateFlow(repository.getSetting("location", "GPS"))
    val location = _location.asStateFlow()

    private var _windSpeed = MutableStateFlow(repository.getSetting("windSpeed", "Meter/sec"))
    val windSpeed = _windSpeed.asStateFlow()

    init {
        loadWeatherAndForecast()
    }

    fun getWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val weatherModel = repository.getWeather(lat, lon, units.value, language.value)
                _weather.value = weatherModel
                Log.d("HomeViewModel", "Weather data fetched: $weatherModel")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching weather data: ${e.message}")
            }
        }
    }

    fun getForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val forecastModel = repository.getForecast(lat, lon, units.value, language.value)
                _forecast.value = forecastModel
                Log.d("HomeViewModel", "Forecast data fetched: $forecastModel")
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching forecast data: ${e.message}")
            }
        }
    }

    fun reloadSettings() {
        _language.value = repository.getSetting("language", "en")
        _units.value = repository.getSetting("temperature", "Celsius")
        _location.value = repository.getSetting("location", "GPS")
        _windSpeed.value = repository.getSetting("windSpeed", "Meter/sec")
        loadWeatherAndForecast()
    }

    private fun loadWeatherAndForecast() {
        getWeather(31.197729, 29.892540) // Default coordinates (Alexandria)
        getForecast(31.197729, 29.892540)
    }

    // Temperature conversion function
    fun convertTemperature(temp: Double, unit: String): Double {
        return when (unit) {
            "Celsius" -> temp - 273.15 // Convert from Kelvin to Celsius
            "Fahrenheit" -> (temp - 273.15) * 9 / 5 + 32 // Convert from Kelvin to Fahrenheit
            "Kelvin" -> temp // No conversion needed
            else -> temp
        }
    }
    fun formatTemperature(temp: Double): String {
        return String.format("%.2f", temp)
    }
}

class WeatherFactory(private val repo: Repo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
