package com.youssef.weatherforcast.Navigation


import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

//@Composable
//fun HomeScreen() { CenteredScreen("Home Screen") }

@Composable
fun FavouriteScreen() { CenteredScreen("Favourite Screen") }

@Composable
fun AlertsScreen() { CenteredScreen("Alerts Screen") }

@Composable
fun SettingsScreen() { CenteredScreen("Settings Screen") }

@Composable
fun CenteredScreen(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text)
    }
}

