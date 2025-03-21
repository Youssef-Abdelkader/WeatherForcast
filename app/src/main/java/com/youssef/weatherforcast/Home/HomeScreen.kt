package com.youssef.weatherforcast.Home

import SettingsViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.youssef.weatherforcast.Model.ForecastResponse
import com.youssef.weatherforcast.Model.WeatherResponse
import com.youssef.weatherforcast.R
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(homeViewModel: HomeViewModel, settingsViewModel: SettingsViewModel) {
    val weatherState by homeViewModel.weather.collectAsState()
    val forecastState by homeViewModel.forecast.collectAsState()
    val language by homeViewModel.language.collectAsState()
    val units by homeViewModel.units.collectAsState()
    val location by homeViewModel.location.collectAsState()
    val windSpeedUnit by homeViewModel.windSpeed.collectAsState()
    val settingsUpdated by settingsViewModel.settingsUpdated.collectAsState()

    LaunchedEffect(settingsUpdated) {
        if (settingsUpdated) {
            homeViewModel.reloadSettings()
            settingsViewModel.notifySettingsChanged(false)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E3A8A), Color(0xFF0F4C81))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            weatherState?.let { weather ->
                WeatherCard(weather, windSpeedUnit, units, homeViewModel)
                Spacer(modifier = Modifier.height(16.dp))
            } ?: Text(
                text = stringResource(R.string.loading_weather),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            forecastState?.let { forecast ->
                if (!forecast.list.isNullOrEmpty()) {
                    Text(
                        text = stringResource(R.string.next_hours),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 8.dp)
                    )
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(forecast.list.take(8)) { item ->
                            HourlyForecastItem(item, weatherState, homeViewModel, units)
                        }
                    }
                }
            }

            forecastState?.let { forecast ->
                if (!forecast.list.isNullOrEmpty()) {
                    val groupedForecast = groupForecastByDay(forecast.list)
                    Text(
                        text = stringResource(R.string.next_days),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 8.dp)
                    )
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(groupedForecast) { item ->
                            ForecastItem(item, weatherState, homeViewModel, units)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.no_forecast_data),
                        color = Color.White,
                        modifier = Modifier.padding(16.dp))
                }
            } ?: Text(
                text = stringResource(R.string.loading_forecast),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun WeatherCard(
    weather: WeatherResponse,
    windSpeedUnit: String,
    temperatureUnit: String,
    homeViewModel: HomeViewModel
) {
    val convertedTemp = homeViewModel.convertTemperature(weather.main.temp, temperatureUnit)
    val formattedTemp = homeViewModel.formatTemperature(convertedTemp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(10.dp, shape = RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(40.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3A3A3A).copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF6A11CB),
                            Color(0xFF2575FC))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
                .fillMaxWidth())
        {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = weather.name ?: stringResource(R.string.unknown_city),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val iconCode = weather.weather.firstOrNull()?.icon ?: "01d"
            Image(
                painter = painterResource(id = weather.weatherIconResourceId(iconCode)),
                contentDescription = stringResource(R.string.weather_icon),
                modifier = Modifier.size(100.dp)
            )

            Text(
                text = "$formattedTemp°${when (temperatureUnit) {
                    "Celsius" -> "C"
                    "Fahrenheit" -> "F"
                    "Kelvin" -> "K"
                    else -> "C"
                }}",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = weather.weather.firstOrNull()?.description
                    ?: stringResource(R.string.unknown),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem(
                    stringResource(R.string.humidity),
                    "${weather.main.humidity}%",
                    Color.White
                )
                WeatherDetailItem(
                    stringResource(R.string.wind),
                    "${weather.wind.speed} ${if (windSpeedUnit == "Meter/sec") "m/s" else "mph"}",
                    Color.White
                )
                WeatherDetailItem(
                    stringResource(R.string.pressure),
                    "${weather.main.pressure} hPa",
                    Color.White
                )
            }
        }
    }
    }
}

@Composable
fun WeatherDetailItem(label: String, value: String, textColor: Color = Color.White) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = textColor.copy(alpha = 0.8f))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor)
    }
}

@Composable
fun HourlyForecastItem(
    item: ForecastResponse.Item0,
    weatherResponse: WeatherResponse?,
    homeViewModel: HomeViewModel,
    temperatureUnit: String
) {
    val convertedTemp = homeViewModel.convertTemperature(item.main.temp, temperatureUnit)
    val formattedTemp = homeViewModel.formatTemperature(convertedTemp)

    Card(
        modifier = Modifier
            .width(100.dp)
            .padding(4.dp)
            .shadow(10.dp, shape = RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF6A11CB),
                            Color(0xFF2575FC))
                    )
                )
                .padding(8.dp)
                .fillMaxWidth())
         {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.dt_txt?.substring(11, 16) ?: stringResource(R.string.na),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )

            val iconCode = item.weather.firstOrNull()?.icon ?: "01d"
            Image(
                painter = painterResource(
                    id = weatherResponse?.weatherIconResourceId(iconCode) ?: R.drawable.day_clear
                ),
                contentDescription = stringResource(R.string.weather_icon),
                modifier = Modifier.size(40.dp)
            )

            Text(
                text = "$formattedTemp°${when (temperatureUnit) {
                    "Celsius" -> "C"
                    "Fahrenheit" -> "F"
                    "Kelvin" -> "K"
                    else -> "C"
                }}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
    }
}

@Composable
fun ForecastItem(
    item: ForecastResponse.Item0,
    weatherResponse: WeatherResponse?,
    homeViewModel: HomeViewModel,
    temperatureUnit: String
) {
    val convertedTemp = homeViewModel.convertTemperature(item.main.temp, temperatureUnit)
    val formattedTemp = homeViewModel.formatTemperature(convertedTemp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(10.dp, shape = RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF6A11CB),
                            Color(0xFF2575FC))
                    )
                )
                .padding(16.dp)
                .fillMaxWidth())
         {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val iconCode = item.weather.firstOrNull()?.icon ?: "01d"
            Image(
                painter = painterResource(
                    id = weatherResponse?.weatherIconResourceId(iconCode) ?: R.drawable.day_clear
                ),
                contentDescription = stringResource(R.string.weather_icon),
                modifier = Modifier.size(50.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = item.dt_txt?.substring(0, 10) ?: stringResource(R.string.no_date),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Text(
                    text = "${stringResource(R.string.temp)}: $formattedTemp°${
                        when (temperatureUnit) {
                            "Celsius" -> "C"
                            "Fahrenheit" -> "F"
                            "Kelvin" -> "K"
                            else -> "C"
                        }
                    }",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Text(
                text = item.weather.firstOrNull()?.description
                    ?: stringResource(R.string.no_data),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
    }
}

fun groupForecastByDay(forecastList: List<ForecastResponse.Item0>): List<ForecastResponse.Item0> {
    val groupedForecast = mutableListOf<ForecastResponse.Item0>()
    var currentDate = ""
    for (item in forecastList) {
        val date = item.dt_txt.substring(0, 10)
        if (date != currentDate) {
            groupedForecast.add(item)
            currentDate = date
        }
    }
    return groupedForecast
}