package com.youssef.weatherforcast.Navigation

import SettingsViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.youssef.weatherforcast.Favourite.DetailedScreen
import com.youssef.weatherforcast.Favourite.FavoriteScreen
import com.youssef.weatherforcast.Favourite.FavoriteViewModel
import com.youssef.weatherforcast.Favourite.MapScreen
import com.youssef.weatherforcast.Home.HomeScreen
import com.youssef.weatherforcast.Home.HomeViewModel
import com.youssef.weatherforcast.Model.Repo
import com.youssef.weatherforcast.Setting.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    repo: Repo,
    homeViewModel: HomeViewModel,
    favoriteViewModel: FavoriteViewModel,
    settingsViewModel: SettingsViewModel
) {
    NavHost(navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) { SplashScreen(navController) }
        composable(Screen.Home.route) {
            HomeScreen(homeViewModel, settingsViewModel)
        }
        composable(Screen.Favourite.route) {
            FavoriteScreen(
                navController = navController,
                repo = repo,
                favoriteViewModel = favoriteViewModel
            ) { cityName, lat, lon ->
                navController.navigate("detailed_screen/$cityName/$lat/$lon")
            }
        }
        composable(Screen.Alerts.route) { AlertsScreen() }

        // Fixed composable definition (no parameter duplication)
        composable(Screen.Detailed.route) { backStackEntry ->
            val cityName = backStackEntry.arguments?.getString("cityName") ?: ""
            val lat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull() ?: 0.0
            val lon = backStackEntry.arguments?.getString("lon")?.toDoubleOrNull() ?: 0.0

            DetailedScreen(
                homeViewModel = homeViewModel,
                settingsViewModel = settingsViewModel,
                cityName = cityName,
                lat = lat,
                lon = lon
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(settingsViewModel)
        }
        composable(Screen.Map.route) {
            MapScreen(navController, repo)
        }
    }
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Favourite : Screen("favourite")
    object Alerts : Screen("alerts")
    object Settings : Screen("settings")
    object Map : Screen("map_screen")
    object Detailed : Screen("detailed_screen/{cityName}/{lat}/{lon}")
}