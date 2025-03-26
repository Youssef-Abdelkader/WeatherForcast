package com.youssef.weatherforcast.Favourite
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.youssef.weatherforcast.Data.LocalDataSource.AppDatabase
import com.youssef.weatherforcast.Data.RemoteDataSource.ApiService
import com.youssef.weatherforcast.Data.RemoteDataSource.RemoteDataSourceImpl
import com.youssef.weatherforcast.Data.RemoteDataSource.RetrofitHelper
import com.youssef.weatherforcast.Model.FavoriteLocation
import com.youssef.weatherforcast.Model.Repo
import com.youssef.weatherforcast.Model.RepoImpl
import com.youssef.weatherforcast.Navigation.Screen
import com.youssef.weatherforcast.Setting.SettingsPreferences

@Composable
fun FavoriteScreen(navController: NavController, repo: Repo,favoriteViewModel: FavoriteViewModel) {
    val viewModel: FavoriteViewModel = viewModel(factory = FavoriteFactory(repo))

    val favorites by viewModel.favorites.collectAsState(emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.Map.route)

                },
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
                    FavoriteItem(location, onRemove = { viewModel.removeFavorite(location) })
                }
            }
        }
    }
}
@Composable
fun FavoriteItem(location: FavoriteLocation, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .shadow(8.dp, shape = RoundedCornerShape(16.dp)),
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
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Location",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = location.locationName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lat: ${location.latitude}, Lon: ${location.longitude}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFFFF5252).copy(alpha = 0.2f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = Color(0xFFFF5252)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewFavoriteItem() {
    FavoriteItem(
        location = FavoriteLocation(id = 1, locationName = "Cairo", latitude = 30.033, longitude = 31.233),
        onRemove = {}
    )
}
