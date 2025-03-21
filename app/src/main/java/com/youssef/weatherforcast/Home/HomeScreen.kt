package com.youssef.weatherforcast.Home
//////////last edit from chatgpt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.youssef.weatherforcast.Model.ForecastResponse
import com.youssef.weatherforcast.Model.WeatherResponse
import com.youssef.weatherforcast.R
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val weatherState by viewModel.weather.collectAsState()
    val forecastState by viewModel.forecast.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getWeather(lat = 31.197729, lon = 29.892540, units = "metric", language = "en")
        viewModel.getForecast(lat = 31.197729, lon = 29.892540, units = "metric", language = "en")
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
                WeatherCard(weather)
                Spacer(modifier = Modifier.height(16.dp))
            } ?: Text(
                text = "Loading current weather...",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )

            forecastState?.let { forecast ->
                if (!forecast.list.isNullOrEmpty()) {
                    Text(
                        text = "NEXT HOURS",
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
                        items(forecast.list.take(8)) {
                            HourlyForecastItem(it, weatherState)
                        }
                    }
                }
            }

            forecastState?.let { forecast ->
                if (!forecast.list.isNullOrEmpty()) {
                    val groupedForecast = groupForecastByDay(forecast.list)
                    Text(
                        text = "NEXT DAYS",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(bottom = 8.dp)
                    )
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(groupedForecast) {
                            ForecastItem(it, weatherState)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                } else {
                    Text(
                        text = "No forecast data available",
                        color = Color.White,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } ?: Text(
                text = "Loading forecast...",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
@Composable
fun WeatherCard(weather: WeatherResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent) // Set container color to transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF6A11CB), // Deep purple
                            Color(0xFF2575FC)  // Bright blue
                        )
                    )
                )
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = weather.name ?: "Unknown City",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White, // White text for better contrast
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val iconCode = weather.weather.firstOrNull()?.icon ?: "01d"
                Image(
                    painter = painterResource(id = weather.weatherIconResourceId(iconCode)),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(100.dp)
                )

                Text(
                    text = "${weather.main.temp}°C",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White, // White text for better contrast
                    modifier = Modifier.padding(top = 8.dp)
                )

                Text(
                    text = weather.weather.firstOrNull()?.description ?: "Unknown",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White.copy(alpha = 0.8f), // Slightly transparent white
                    modifier = Modifier.padding(top = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeatherDetailItem("Humidity", "${weather.main.humidity}%", Color.White)
                    WeatherDetailItem("Wind", "${weather.wind.speed} m/s", Color.White)
                    WeatherDetailItem("Pressure", "${weather.main.pressure} hPa", Color.White)
                }
            }
        }
    }
}

@Composable
fun WeatherDetailItem(label: String, value: String, textColor: Color = Color.White) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.titleSmall, color = textColor.copy(alpha = 0.8f))
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = textColor)
    }
}@Composable
fun HourlyForecastItem(item: ForecastResponse.Item0, weatherResponse: WeatherResponse?) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent) // Transparent background
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF6A11CB), // Deep purple
                            Color(0xFF2575FC)  // Bright blue
                        )
                    )
                )
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.dt_txt?.substring(11, 16) ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White // White text for contrast
                )

                val iconCode = item.weather.firstOrNull()?.icon ?: "01d"
                Image(
                    painter = painterResource(id = weatherResponse?.weatherIconResourceId(iconCode) ?: R.drawable.day_clear),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(40.dp)
                )

                Text(
                    text = "${item.main.temp}°C",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White // White text for contrast
                )
            }
        }
    }
}
@Composable
fun ForecastItem(item: ForecastResponse.Item0, weatherResponse: WeatherResponse?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent) // Transparent background
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF6A11CB), // Deep purple
                            Color(0xFF2575FC)  // Bright blue
                        )
                    )
                )
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Add space between icon and text
            ) {
                val iconCode = item.weather.firstOrNull()?.icon ?: "01d"
                Image(
                    painter = painterResource(id = weatherResponse?.weatherIconResourceId(iconCode) ?: R.drawable.day_clear),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(50.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = item.dt_txt?.substring(0, 10) ?: "No Date",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White // White text for contrast
                    )
                    Text(
                        text = "Temp: ${item.main.temp}°C",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f) // Slightly transparent white
                    )
                }

                Text(
                    text = item.weather.firstOrNull()?.description ?: "No data",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f) // Slightly transparent white
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
////////////////the last edit