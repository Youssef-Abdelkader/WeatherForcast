package com.youssef.weatherforcast

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.youssef.weatherforcast.Data.LocalDataSource.AppDatabase
import com.youssef.weatherforcast.Data.RemoteDataSource.RemoteDataSourceImpl
import com.youssef.weatherforcast.Data.RemoteDataSource.RetrofitHelper
import com.youssef.weatherforcast.Favourite.FavoriteFactory
import com.youssef.weatherforcast.Favourite.FavoriteViewModel
import com.youssef.weatherforcast.Home.HomeViewModel
import com.youssef.weatherforcast.Home.WeatherFactory
import com.youssef.weatherforcast.Model.RepoImpl
import com.youssef.weatherforcast.Navigation.AppNavHost
import com.youssef.weatherforcast.Navigation.BottomNavigationBar
import com.youssef.weatherforcast.Navigation.Screen
import com.youssef.weatherforcast.Setting.SettingsPreferences
import com.youssef.weatherforcast.Setting.SettingsViewModel
import com.youssef.weatherforcast.Setting.SettingsViewModelFactory
import com.youssef.weatherforcast.ui.theme.WeatherForcastTheme
import com.youssef.weatherforcast.utils.isInternetAvailable
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var locationManager: LocationManager
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var settingsPreferences: SettingsPreferences

    private val REQUEST_LOCATION_PERMISSION = 1001
    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = _isConnected

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isConnected.postValue(true)
        }

        override fun onLost(network: Network) {
            _isConnected.postValue(false)
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (settingsViewModel.selectedLocation.value == "GPS") {
                val lat = location.latitude
                val lon = location.longitude
                homeViewModel.loadWeatherAndForecast(lat, lon)
                homeViewModel.clearCoordinates()
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize settings preferences first
        settingsPreferences = SettingsPreferences(this)
        applyLanguage()

        // Check for exact alarms permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            }
        }

        // Initialize dependencies
        val apiService = RetrofitHelper.service
        val remoteDataSource = RemoteDataSourceImpl.getInstance(apiService)
        val database = AppDatabase.getInstance(applicationContext)
        val favoriteDao = database.favoriteDao()

        val repo = RepoImpl(remoteDataSource, settingsPreferences, favoriteDao)

        homeViewModel = ViewModelProvider(this, WeatherFactory(repo))[HomeViewModel::class.java]
        favoriteViewModel = ViewModelProvider(this, FavoriteFactory(repo))[FavoriteViewModel::class.java]
        settingsViewModel = ViewModelProvider(this, SettingsViewModelFactory(repo))[SettingsViewModel::class.java]

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        checkNetworkConnection()

        setContent {
            WeatherForcastTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route
                val isBottomBarVisible = currentRoute != Screen.Splash.route

                Scaffold(
                    bottomBar = {
                        if (isBottomBarVisible) {
                            BottomNavigationBar(navController)
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        AppNavHost(
                            navController,
                            repo,
                            homeViewModel,
                            favoriteViewModel,
                            settingsViewModel
                        )

                        LaunchedEffect(Unit) {
                            settingsViewModel.selectedLocation.collect { locationMode ->
                                if (locationMode == "GPS") checkLocationPermissions()
                            }
                        }
                    }
                }
            }
        }

        checkLocationPermissions()
    }

    private fun checkLocationPermissions() {
        when {
            hasLocationPermissions() -> {
                try {
                    locationManager.removeUpdates(locationListener)
                    requestLocationUpdates()
                } catch (e: SecurityException) {
                    Log.e("Location", "Security Exception: ${e.message}")
                }
            }
            else -> requestLocationPermission()
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_PERMISSION
        )
    }

    private fun requestLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000L,
                10f,
                locationListener
            )
        } catch (e: SecurityException) {
            Log.e("Location", "Security Exception: ${e.message}")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationUpdates()
            } else {
                Log.e("Permission", "Location permission denied!")
                homeViewModel.loadWeatherAndForecast(30.033, 31.233)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(locationListener)
    }

    private fun applyLanguage() {
        val langSetting = settingsPreferences.getSetting("language", "Default")
        val languageCode = when (langSetting) {
            "Arabic" -> "ar"
            "English" -> "en"
            else -> {
                val systemLang = settingsPreferences.getSystemLocale().language
                if (systemLang in listOf("ar", "en")) systemLang else "en"
            }
        }

        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun checkNetworkConnection() {
        _isConnected.postValue(isInternetAvailable(this))
        if (!isInternetAvailable(this)) homeViewModel.checkInternet()
    }

    override fun onResume() {
        super.onResume()
        checkNetworkConnection()
    }
}