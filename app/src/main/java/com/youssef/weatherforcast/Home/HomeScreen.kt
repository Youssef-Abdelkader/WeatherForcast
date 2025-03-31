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
    val settingsUpdated by settingsViewModel.settingsUpdated.collectAsState()

    // Collect the selected temperature and wind speed units
    val temperatureUnit by settingsViewModel.selectedTemperature.collectAsState()
    val windSpeedUnit by settingsViewModel.selectedWindSpeed.collectAsState()

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
                    colors = listOf(Color(0xFF2193b0), Color(0xFF6dd5ed))
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                weatherState?.let { weather ->
                    WeatherCard(weather, homeViewModel, temperatureUnit)
                    Spacer(modifier = Modifier.height(8.dp))
                    WeatherDetailsCard(weather, homeViewModel, temperatureUnit, windSpeedUnit)
                    Spacer(modifier = Modifier.height(8.dp))
                    WindSpeedCard(weather, settingsViewModel) // ✅ تمت إضافته هنا
                    Spacer(modifier = Modifier.height(15.dp))
                    Spacer(modifier = Modifier.height(15.dp))
                } ?: Text(
                    text = stringResource(R.string.loading_weather),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }

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
                                HourlyForecastItem(
                                    item, weatherState, homeViewModel,
                                    temperatureUnit = temperatureUnit
                                )
                            }
                        }
                    }
                }
            }

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
                        ForecastItem(
                            item, weatherState, homeViewModel,
                            temperatureUnit = temperatureUnit
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
fun WeatherCard(weather: WeatherResponse, homeViewModel: HomeViewModel, temperatureUnit: String) {
    val convertedTemp = homeViewModel.convertTemperature(weather.main.temp, temperatureUnit)
    val formattedTemp = homeViewModel.formatTemperature(convertedTemp)

    val formattedDate = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(System.currentTimeMillis())
    val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(System.currentTimeMillis())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .shadow(8.dp, shape = RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3A3A3A).copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E90FF), Color(0xFF00BFFF))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = weather.name ?: stringResource(R.string.unknown_city),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            val iconCode = weather.weather.firstOrNull()?.icon ?: "01d"
            Image(
                painter = painterResource(id = weather.weatherIconResourceId(iconCode)),
                contentDescription = stringResource(R.string.weather_icon),
                modifier = Modifier.size(80.dp)
            )

            Text(
                text = "$formattedTemp°${
                    when (temperatureUnit) {
                        "Celsius" -> "C"
                        "Fahrenheit" -> "F"
                        "Kelvin" -> "K"
                        else -> "C"
                    }
                }",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White
            )

            Text(
                text = weather.weather.firstOrNull()?.description ?: stringResource(R.string.unknown),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$formattedDate | $formattedTime",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun WeatherDetailsCard(weather: WeatherResponse, homeViewModel: HomeViewModel, temperatureUnit: String, windSpeedUnit: String) {
    val sunriseTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(weather.sys.sunrise * 1000L)
    val sunsetTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(weather.sys.sunset * 1000L)

    // Convert min/max temperature
    val tempMin = homeViewModel.formatTemperature(homeViewModel.convertTemperature(weather.main.temp_min, temperatureUnit))
    val tempMax = homeViewModel.formatTemperature(homeViewModel.convertTemperature(weather.main.temp_max, temperatureUnit))
    val tempUnitSymbol = when (temperatureUnit) {
        "Celsius" -> "°C"
        "Fahrenheit" -> "°F"
        "Kelvin" -> "°K"
        else -> "°C"
    }

    // Convert wind speed
    val convertedWindSpeed = when (windSpeedUnit) {
        "m/s" -> weather.wind.speed  // Keep as is
        "mph" -> weather.wind.speed * 2.237 // Convert from m/s to mph
        else -> weather.wind.speed
    }
    val windSpeedFormatted = homeViewModel.formatTemperature(convertedWindSpeed)
    val windSpeedUnitSymbol = if (windSpeedUnit == "mph") "mph" else "m/s"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .shadow(10.dp, shape = RoundedCornerShape(18.dp)),
        elevation = CardDefaults.cardElevation(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3A3A3A).copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2193b0), Color(0xFF6dd5ed))
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItemWithIcon(
                    R.drawable.humidity,
                    stringResource(R.string.humidity),
                    "${weather.main.humidity}%",
                    Color.White
                )
                Spacer(modifier = Modifier.width(16.dp))
                WeatherDetailItemWithIcon(
                    R.drawable.pressure,
                    stringResource(R.string.pressure),
                    "${weather.main.pressure} hPa",
                    Color.White
                )
                Spacer(modifier = Modifier.width(16.dp))
                WeatherDetailItemWithIcon(
                    R.drawable.sealevel,
                    stringResource(R.string.sea_level),
                    "${weather.main.sea_level} hPa",
                    Color.White
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItemWithIcon(
                    R.drawable.sunrise,
                    stringResource(R.string.sunrise),
                    sunriseTime,
                    Color.White
                )
                Spacer(modifier = Modifier.width(16.dp))
                WeatherDetailItemWithIcon(
                    R.drawable.sunset,
                    stringResource(R.string.sunset),
                    sunsetTime,
                    Color.White
                )
                Spacer(modifier = Modifier.width(16.dp))
                WeatherDetailItemWithIcon(
                    R.drawable.tempreture,
                    stringResource(R.string.temp),
                    "$tempMin° / $tempMax° $tempUnitSymbol",
                    Color.White
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Wind Speed Row

        }
    }
}

@Composable
fun WindSpeedCard(weather: WeatherResponse, settingsViewModel: SettingsViewModel) {
    val windSpeedUnit by settingsViewModel.selectedWindSpeed.collectAsState()

    // Convert wind speed based on user preference
    val windSpeed = when (windSpeedUnit) {
        "Meter/sec" -> weather.wind.speed  // m/s
        "Mile/hour" -> weather.wind.speed * 2.23694  // Convert to mph
        else -> weather.wind.speed
    }
    val formattedWindSpeed = String.format("%.1f", windSpeed)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .shadow(10.dp, shape = RoundedCornerShape(18.dp)),
        elevation = CardDefaults.cardElevation(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3A3A3A).copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF2193b0), Color(0xFF6dd5ed))
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(vertical = 16.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.wind),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                WeatherDetailItemWithIcon(
                    iconResId = R.drawable.windspeed,
                    label = stringResource(R.string.wind),
                    value = "$formattedWindSpeed ${
                        when (windSpeedUnit) {
                            "Meter/sec" -> "m/s"
                            "Mile/hour" -> "mph"
                            else -> "m/s"
                        }
                    }",
                    textColor = Color.White
                )
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

