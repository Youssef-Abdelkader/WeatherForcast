package com.youssef.weatherforcast.Favourite
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.youssef.weatherforcast.Data.LocalDataSource.AppDatabase
import com.youssef.weatherforcast.Data.RemoteDataSource.ApiService
import com.youssef.weatherforcast.Data.RemoteDataSource.RemoteDataSourceImpl
import com.youssef.weatherforcast.Data.RemoteDataSource.RetrofitHelper
import com.youssef.weatherforcast.Model.FavoriteLocation
import com.youssef.weatherforcast.Model.ForecastResponse
import com.youssef.weatherforcast.Model.Repo
import com.youssef.weatherforcast.Model.RepoImpl
import com.youssef.weatherforcast.Model.WeatherResponse
import com.youssef.weatherforcast.Navigation.Screen
import com.youssef.weatherforcast.R
import com.youssef.weatherforcast.Setting.SettingsPreferences
import kotlin.math.roundToInt

@Composable
fun FavoriteScreen(
    navController: NavController,
    repo: Repo,
    favoriteViewModel: FavoriteViewModel,
    navToHome: (cityName: String, lat: Double, lon: Double) -> Unit
) {
    val viewModel: FavoriteViewModel = viewModel(factory = FavoriteFactory(repo))
    val favorites by viewModel.favorites.collectAsState(emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.Map.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(text = "Favorite", style = MaterialTheme.typography.headlineSmall)

            LazyColumn {
                items(favorites) { location ->
                    val weatherState = produceState<WeatherResponse?>(initialValue = null) {
                        // Collect the Flow from getWeatherSafely
                        viewModel.getWeatherSafely(
                            location.latitude, location.longitude,
                            unitParam = viewModel.units.value,
                        )
                            ?.collect { weather ->
                                value = weather
                            }
                    }

                    FavoriteItem(
                        location = location,
                        weather = weatherState.value,
                        onRemove = { viewModel.removeFavorite(location) },
                        navToHome = navToHome
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteItem(
    location: FavoriteLocation,
    weather: WeatherResponse?,
    onRemove: () -> Unit,
    navToHome: (cityName: String, lat: Double, lon: Double) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .clickable {
                navToHome(location.locationName, location.latitude, location.longitude)
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF80DEEA), Color(0xFF4DB6AC))
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = "https://flagcdn.com/w320/${location.countryCode.lowercase()}.png",
                    contentDescription = "Country Flag",
                    modifier = Modifier.size(60.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = location.locationName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))

//                    Text(
//                        text = "Lat: %.2f, Lon: %.2f".format(location.latitude, location.longitude),
//                        style = MaterialTheme.typography.bodySmall,
//                        color = Color.White.copy(alpha = 0.6f)
//                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    weather?.let {
                        val celsiusTemp = (it.main.temp - 273.15).roundToInt()
                        Text(
                            text = "Temp: ${celsiusTemp}°C",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Weather Icon
                weather?.let {
                    val iconRes = it.weather.firstOrNull()?.icon?.let { iconCode ->
                        it.weatherIconResourceId(iconCode)
                    } ?: R.drawable.day_clear

                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = "Weather Icon",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Remove Icon
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFFFF5252), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
