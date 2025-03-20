package com.youssef.weatherforcast.Home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youssef.weatherforcast.Model.ForecastResponse
import com.youssef.weatherforcast.Model.Repo
import com.youssef.weatherforcast.Model.RepoImpl
import com.youssef.weatherforcast.Model.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private var repository: Repo) : ViewModel() {
    private var _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather = _weather.asStateFlow()

    private var _forecast = MutableStateFlow<ForecastResponse?>(null)
    val forecast = _forecast.asStateFlow()

    fun getWeather(lat: Double, lon: Double, units: String, language: String) {
        viewModelScope.launch {
            try {
                val weatherModel = repository.getWeather(lat, lon, units, language)
                _weather.value = weatherModel
                Log.d("viewdata", "getWeather: $weatherModel") // Log the response
            } catch (e: Exception) {
                Log.d("viewdata", "getWeather Error: ${e.message}") // Log any errors
            }
        }
    }

    fun getForecast(lat: Double, lon: Double, units: String, language: String) {
        viewModelScope.launch {
            try {
                val forecastModel = repository.getForecast(lat, lon, units, language)
                _forecast.value = forecastModel
                Log.d("viewdata", "getForecast: $forecastModel") // Log the response
            } catch (e: Exception) {
                Log.d("viewdata", "getForecast Error: ${e.message}") // Log any errors
            }
        }
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
