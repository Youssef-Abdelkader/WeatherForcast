package com.youssef.weatherforcast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.youssef.weatherforcast.Data.RemoteDataSource.RemoteDataSourceImpl
import com.youssef.weatherforcast.Data.RemoteDataSource.RetrofitHelper
import com.youssef.weatherforcast.Home.HomeViewModel
import com.youssef.weatherforcast.Home.WeatherFactory
import com.youssef.weatherforcast.Model.RepoImpl
import com.youssef.weatherforcast.ui.theme.WeatherForcastTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var viewModel: HomeViewModel= ViewModelProvider(this, WeatherFactory( RepoImpl( RemoteDataSourceImpl.getInstance( RetrofitHelper.service)))).get(HomeViewModel::class.java)
               viewModel.getWeather(30.0444,31.2357,"metric","en")
               viewModel.getForecast(30.0444,31.2357,"metric","en")

        setContent {
            WeatherForcastTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeatherForcastTheme {
        Greeting("Android")
    }
}