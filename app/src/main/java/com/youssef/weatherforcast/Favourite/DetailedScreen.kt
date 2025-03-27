package com.youssef.weatherforcast.Favourite

import com.youssef.weatherforcast.Home.HomeViewModel


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
fun DetailedScreen(homeViewModel: HomeViewModel, settingsViewModel: SettingsViewModel, cityName: String, lat:Double, lon:Double) {
    val weatherState by homeViewModel.weather.collectAsState()
    val forecastState by homeViewModel.forecast.collectAsState()
    val language by homeViewModel.language.collectAsState()
    val units by homeViewModel.units.collectAsState()
    val location by homeViewModel.location.collectAsState()
    val windSpeedUnit by homeViewModel.windSpeed.collectAsState()
    val settingsUpdated by settingsViewModel.settingsUpdated.collectAsState()

    homeViewModel.loadWeatherAndForecast(lat, lon)
    LaunchedEffect(settingsUpdated) {
        if (settingsUpdated) {

            homeViewModel.reloadSettings()
            homeViewModel.reloadData()
            settingsViewModel.notifySettingsChanged(false)
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF2193b0), Color(0xFF6dd5ed)) // Brighter Blue to Cyan
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ✅ عرض بيانات الطقس الرئيسية
            item {
                weatherState?.let { weather ->
                    WeatherCard(weather, windSpeedUnit, units, homeViewModel)
                    Spacer(modifier = Modifier.height(15.dp))
                } ?: Text(
                    text = stringResource(R.string.loading_weather),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // ✅ التوقعات لكل ساعة
            item {
                forecastState?.let { forecast ->
                    if (!forecast.list.isNullOrEmpty()) {
                        Text(
                            text = stringResource(R.string.next_hours),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(forecast.list.take(8)) { item ->
                                HourlyForecastItem(item, weatherState, homeViewModel, units)
                            }
                        }
                    }
                }
            }

            // ✅ التوقعات الأسبوعية
            forecastState?.let { forecast ->
                if (!forecast.list.isNullOrEmpty()) {
                    val groupedForecast = groupForecastByDay(forecast.list)

                    item {
                        Text(
                            text = stringResource(R.string.next_days),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(groupedForecast) { item ->
                        ForecastItem(item, weatherState, homeViewModel, units)
                    }
                } else {
                    item {
                        Text(
                            text = stringResource(R.string.no_forecast_data),
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } ?: item {
                Text(
                    text = stringResource(R.string.loading_forecast),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
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

    val sunriseTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(weather.sys.sunrise * 1000L)
    val sunsetTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(weather.sys.sunset * 1000L)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .shadow(8.dp, shape = RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(40.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3A3A3A).copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E90FF), Color(0xFF00BFFF), Color(0xFF87CEFA)) // Deep Sky Blue to Light Sky Blue
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // City Name
                Text(
                    text = weather.name ?: stringResource(R.string.unknown_city),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Weather Icon
                val iconCode = weather.weather.firstOrNull()?.icon ?: "01d"
                Image(
                    painter = painterResource(id = weather.weatherIconResourceId(iconCode)),
                    contentDescription = stringResource(R.string.weather_icon),
                    modifier = Modifier.size(100.dp)
                )

                // Temperature
                Text(
                    text = "$formattedTemp°${when (temperatureUnit) {
                        "Celsius" -> "C"
                        "Fahrenheit" -> "F"
                        "Kelvin" -> "K"
                        else -> "C"
                    }}",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    modifier = Modifier.padding(top = 2.dp)
                )

                // Weather Description
                Text(
                    text = weather.weather.firstOrNull()?.description ?: stringResource(R.string.unknown),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Weather Details (Humidity, Wind, Pressure)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeatherDetailItemWithIcon(
                        iconResId = R.drawable.humidity,
                        label = stringResource(R.string.humidity),
                        value = "${weather.main.humidity}%",
                        textColor = Color.White
                    )
                    WeatherDetailItemWithIcon(
                        iconResId = R.drawable.windspeed,
                        label = stringResource(R.string.wind),
                        value = "${weather.wind.speed} ${if (windSpeedUnit == "Meter/sec") "m/s" else "mph"}",
                        textColor = Color.White
                    )
                    WeatherDetailItemWithIcon(
                        iconResId = R.drawable.pressure,
                        label = stringResource(R.string.pressure),
                        value = "${weather.main.pressure} hPa",
                        textColor = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sunrise, Sunset, Sea Level
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeatherDetailItemWithIcon(
                        iconResId = R.drawable.sunrise,
                        label = stringResource(R.string.sunrise),
                        value = sunriseTime,
                        textColor = Color.White
                    )

                    WeatherDetailItemWithIcon(
                        iconResId = R.drawable.sunset,
                        label = stringResource(R.string.sunset),
                        value = sunsetTime,
                        textColor = Color.White
                    )

                    WeatherDetailItemWithIcon(
                        iconResId = R.drawable.sealevel,
                        label = stringResource(R.string.sea_level),
                        value = "${weather.main.sea_level} hPa",
                        textColor = Color.White
                    )
                }
            }
        }
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
            .fillMaxSize().padding(8.dp),
        shape = RoundedCornerShape(40.dp),
        //colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2193b0).copy(alpha = 1f),
                            Color(0xFF6dd5ed).copy(alpha = 1f)
                        )
                    ),
                    shape = RoundedCornerShape(40.dp)
                )
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ✅ وقت التوقع
                Text(
                    text = item.dt_txt?.substring(11, 16) ?: stringResource(R.string.na),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                val iconCode = item.weather.firstOrNull()?.icon ?: "01d"
                Image(
                    painter = painterResource(
                        id = weatherResponse?.weatherIconResourceId(iconCode) ?: R.drawable.day_clear
                    ),
                    contentDescription = stringResource(R.string.weather_icon),
                    modifier = Modifier.size(60.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$formattedTemp°${
                        when (temperatureUnit) {
                            "Celsius" -> "C"
                            "Fahrenheit" -> "F"
                            "Kelvin" -> "K"
                            else -> "C"
                        }
                    }",
                    style = MaterialTheme.typography.bodyLarge,
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
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2193b0), Color(0xFF6dd5ed)) // Brighter Blue to Cyan
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

@Composable
fun WeatherDetailItemWithIcon(
    iconResId: Int,
    label: String,
    value: String,
    textColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = "$label\n$value", // "\n" moves the value to a new line
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

////////last edit

