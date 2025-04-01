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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
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
    val locationMode by settingsViewModel.selectedLocation.collectAsState()

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
                    colors = listOf(Color(0xFFAEAEAE), Color(0xFFFCFDFE))
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
                    WeatherCard(weather, homeViewModel, temperatureUnit,locationMode)
                    Spacer(modifier = Modifier.height(15.dp))
                    WeatherDetailsCard(weather, homeViewModel, temperatureUnit,settingsViewModel)
                    Spacer(modifier = Modifier.height(8.dp))
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
                            color = Color.Black,
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
                            color = Color.Black,
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
fun WeatherCard(weather: WeatherResponse, homeViewModel: HomeViewModel, temperatureUnit: String,locationMode:String) {
    val convertedTemp = homeViewModel.convertTemperature(weather.main.temp, temperatureUnit)
    val formattedTemp = homeViewModel.formatTemperature(convertedTemp)
    val dateFormat = remember {
        SimpleDateFormat("EEEE, MMM d", homeViewModel.repository.getAppLocale())
    }
    val timeFormat = remember {
        SimpleDateFormat("hh:mm a", homeViewModel.repository.getAppLocale())
    }
    val formattedDate = dateFormat.format(System.currentTimeMillis())
    val formattedTime = timeFormat.format(System.currentTimeMillis())

    // Replace the existing unitSymbol code in WeatherCard
    val unitSymbol = homeViewModel.getLocalizedUnit(temperatureUnit)

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
                        colors = listOf(Color(0xFF2193b0), Color(0xFF6dd5ed))
//                        colors = listOf(Color(0xFF1E90FF), Color(0xFF00BFFF))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${weather.name ?: "Unknown"} ",
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
                text = "$formattedTemp$unitSymbol", // Removed explicit ° character
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
fun WeatherDetailsCard(
    weather: WeatherResponse,
    homeViewModel: HomeViewModel,
    temperatureUnit: String,
    settingsViewModel: SettingsViewModel
) {
    val windSpeedUnit by settingsViewModel.selectedWindSpeed.collectAsState()
    val windSpeedUnitLocalized = homeViewModel.getLocalizedUnit(windSpeedUnit)
    val appLocale = homeViewModel.repository.getAppLocale()
    val timeFormat = remember { SimpleDateFormat("hh:mm a", appLocale) }
    val sunriseTime = timeFormat.format(weather.sys.sunrise * 1000L)
    val sunsetTime = timeFormat.format(weather.sys.sunset * 1000L)

    // Convert min/max temperature
    val tempMin = homeViewModel.formatTemperature(
        homeViewModel.convertTemperature(weather.main.temp_min, temperatureUnit)
    )
    val tempMax = homeViewModel.formatTemperature(
        homeViewModel.convertTemperature(weather.main.temp_max, temperatureUnit)
    )
    val tempUnitLocalized = homeViewModel.getLocalizedUnit(temperatureUnit)

    // Format numbers with locale-aware formatting
    val formattedHumidity = homeViewModel.repository.formatNumber(weather.main.humidity.toDouble())
    val formattedPressure = homeViewModel.repository.formatNumber(weather.main.pressure.toDouble())

    // Convert wind speed
    val windSpeed = when (windSpeedUnit) {
        "Meter/sec" -> weather.wind.speed
        "Mile/hour" -> weather.wind.speed * 2.23694
        else -> weather.wind.speed
    }
    val formattedWindSpeed = homeViewModel.repository.formatNumber(windSpeed)

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
                    "$formattedHumidity%",  // Formatted humidity
                    Color.White
                )
                Spacer(modifier = Modifier.width(16.dp))
                WeatherDetailItemWithIcon(
                    R.drawable.pressure,
                    stringResource(R.string.pressure),
                    "$formattedPressure hPa",  // Formatted pressure
                    Color.White
                )
                Spacer(modifier = Modifier.width(16.dp))
                WeatherDetailItemWithIcon(
                    iconResId = R.drawable.windspeed,
                    label = stringResource(R.string.wind),
                    value = "$formattedWindSpeed $windSpeedUnitLocalized",
                    textColor = Color.White
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
                    "$tempMin° / $tempMax° $tempUnitLocalized",
                    Color.White
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
    val unitSymbol = homeViewModel.getLocalizedUnit(temperatureUnit)

    // Time formatting with locale
    val timeFormat = remember {
        SimpleDateFormat("hh:mm a", homeViewModel.repository.getAppLocale())
    }
    val formattedTime = try {
        item.dt_txt?.let {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(it)
            date?.let { timeFormat.format(it) } ?: ""
        } ?: ""
    } catch (e: Exception) {
        item.dt_txt?.substring(11, 16) ?: stringResource(R.string.na)
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        shape = RoundedCornerShape(40.dp),
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
                Text(
                    text = formattedTime,
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
                    text = "$formattedTemp$unitSymbol",
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
    val unitSymbol = homeViewModel.getLocalizedUnit(temperatureUnit)

    // Date formatting with locale
    val dateFormat = remember {
        SimpleDateFormat("EEEE, d MMM", homeViewModel.repository.getAppLocale())
    }
    val formattedDate = try {
        item.dt_txt?.let {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(it)
            date?.let { dateFormat.format(it) } ?: ""
        } ?: ""
    } catch (e: Exception) {
        item.dt_txt?.substring(0, 10) ?: stringResource(R.string.no_date)
    }

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
                        colors = listOf(Color(0xFF2193b0), Color(0xFF6dd5ed))
                    )
                )
                .padding(16.dp)
                .fillMaxWidth())
        {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = if (isLayoutRTL()) Arrangement.Start else Arrangement.SpaceBetween
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
                        text = formattedDate,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White // Changed from Black to White for visibility
                    )
                    Text(
                        text = "${stringResource(R.string.temp)}: $formattedTemp$unitSymbol",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Text(
                    text = item.weather.firstOrNull()?.description
                        ?: stringResource(R.string.no_data),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = if (isLayoutRTL()) TextAlign.Start else TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun isLayoutRTL(): Boolean {
    return LocalLayoutDirection.current == LayoutDirection.Rtl
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

