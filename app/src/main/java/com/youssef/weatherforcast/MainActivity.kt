package com.youssef.weatherforcast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.youssef.weatherforcast.Data.RemoteDataSource.RemoteDataSourceImpl
import com.youssef.weatherforcast.Data.RemoteDataSource.RetrofitHelper
import com.youssef.weatherforcast.Home.HomeViewModel
import com.youssef.weatherforcast.Model.Repo
import com.youssef.weatherforcast.Model.RepoImpl
import com.youssef.weatherforcast.Navigation.AppNavHost
import com.youssef.weatherforcast.Navigation.BottomNavigationBar
import com.youssef.weatherforcast.Navigation.Screen
import com.youssef.weatherforcast.ui.theme.WeatherForcastTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val apiService = RetrofitHelper.service
        val remoteDataSource = RemoteDataSourceImpl.getInstance(apiService)
        val repo = RepoImpl(remoteDataSource)

        setContent {
            WeatherForcastTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val isBottomBarVisible = backStackEntry?.destination?.route != Screen.Splash.route

                Scaffold(
                    bottomBar = {
                        if (isBottomBarVisible) {
                            BottomNavigationBar(navController)
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavHost(navController, repo) // Pass the repo to AppNavHost
                    }
                }
            }
        }
    }
}