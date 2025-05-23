package com.youssef.weatherforcast.Home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youssef.weatherforcast.Model.ForecastResponse
import com.youssef.weatherforcast.Model.HomeData
import com.youssef.weatherforcast.Model.Repo
import com.youssef.weatherforcast.Model.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class HomeViewModel(var repository: Repo) : ViewModel() {

    private var _lat = MutableStateFlow<Double?>(null)
    private var _lon = MutableStateFlow<Double?>(null)

    private var _manualLat = MutableStateFlow<Double?>(null)
    private var _manualLon = MutableStateFlow<Double?>(null)

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
        reloadSettings()
    }

    fun getWeatherAndForecast(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val weatherModel =
                    repository.getWeather(lat, lon, units.value, language.value).first()
                _weather.value = weatherModel

                val forecastModel =
                    repository.getForecast(lat, lon, units.value, language.value).first()
                _forecast.value = forecastModel

                repository.insertHomeData(
                    homeData = HomeData(
                        weather = weatherModel,
                        forecast = forecastModel
                    )
                )
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching data: ${e.message}")
            }
        }
    }

    fun checkInternet() {
        viewModelScope.launch {
            try {
                val homeData = repository.getHomeDate().first()
                if (homeData != null) {
                    _weather.value = homeData.weather
                    _forecast.value = homeData.forecast
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading cached data: ${e.message}")
            }
        }
    }
    fun clearCoordinates() {
        _lat.value = null
        _lon.value = null
    }


    fun reloadSettings() {
        _language.value = repository.getSetting("language", "en")
        _units.value = repository.getSetting("temperature", "Celsius")
        _location.value = repository.getSetting("location", "GPS")
        _windSpeed.value = repository.getSetting("windSpeed", "Meter/sec")
    }

    fun loadWeatherAndForecast(lat: Double, lon: Double) {
        Log.d("HomeViewModel", "Loading data for coordinates: ($lat, $lon)")
        _lat.value = lat
        _lon.value = lon
        getWeatherAndForecast(lat, lon)
    }

    fun reloadData() {
        when (_location.value) {
            "Map" -> {
                _manualLat.value?.let { lat ->
                    _manualLon.value?.let { lon ->
                        loadWeatherAndForecast(lat, lon)
                    }
                }
            }
            "GPS" -> {
                if (_lat.value == null || _lon.value == null) {
                    // Trigger location update in MainActivity
                }
                _lat.value?.let { lat ->
                    _lon.value?.let { lon ->
                        loadWeatherAndForecast(lat, lon)
                    }
                }
            }
        }
    }



    fun convertTemperature(temp: Double, unit: String): Double {
        val converted = when (unit) {
            "Celsius" -> temp - 273.15.roundToInt()
            "Fahrenheit" -> (temp - 273.15) * 9 / 5 + 32
            "Kelvin" -> temp
            else -> temp
        }
        return converted
    }

    fun formatTemperature(temp: Double): String {
        val rounded = Math.round(temp)
        return repository.formatNumber(rounded.toDouble())
    }

    fun getLocalizedUnit(unit: String): String {
        val currentLanguage = repository.getSetting("language", "English")
        return when (unit) {
            "Celsius" -> if (currentLanguage == "Arabic") "م°" else "°C"
            "Fahrenheit" -> if (currentLanguage == "Arabic") "ف°" else "°F"
            "Kelvin" -> if (currentLanguage == "Arabic") "ك°" else "K"
            "Meter/sec" -> if (currentLanguage == "Arabic") "م/ث" else "m/s"
            "Mile/hour" -> if (currentLanguage == "Arabic") "ميل/س" else "mph"
            else -> unit
        }
    }



    fun updateManualCoordinates(lat: Double, lon: Double) {
        _manualLat.value = lat
        _manualLon.value = lon
        loadWeatherAndForecast(lat, lon)
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
