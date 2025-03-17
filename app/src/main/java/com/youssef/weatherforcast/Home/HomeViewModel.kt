package com.youssef.weatherforcast.Home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.youssef.weatherforcast.Model.Repo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private var repository: Repo)
    : ViewModel() {

         fun getWeather(
        lat: Double,
        lon: Double,
        units:String,
        language:String){
       viewModelScope.launch {
           try {
               val WeatherModel= repository.getWeather(lat,lon,units,language)
               Log.d("tagdata", "getWeather: "+WeatherModel.toString())
           }
           catch (e:Exception){  Log.d("tagdata", "getWeather: "+e.message)}
          // Log.d("tagdata", "getWeather: "+WeatherModel.toString())
       }
        }
     fun getForecast(
        lat: Double,
        lon: Double,
        units:String,
        language:String){
        viewModelScope.launch {
             try {
                 val ForecastModel = repository.getForecast(lat, lon, units, language)
                 Log.d("tagdata", "getForecast: " + ForecastModel.toString())
             }
             catch (e:Exception){ Log.d("tagdata", "getForecast: "+e.message)}
        }
        }


    }

class WeatherFactory(private val repo: Repo) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo) as T
    }
}