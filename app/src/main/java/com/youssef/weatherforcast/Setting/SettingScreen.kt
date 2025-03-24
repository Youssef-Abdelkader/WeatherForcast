package com.youssef.weatherforcast.Setting
import SettingsViewModel
import SettingsViewModelFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.youssef.weatherforcast.Model.Repo
import com.youssef.weatherforcast.R

@Composable
fun SettingsScreen(repo: Repo) {
    val viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(repo))

    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val selectedTemperature by viewModel.selectedTemperature.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val selectedWindSpeed by viewModel.selectedWindSpeed.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1E90FF), Color(0xFF00BFFF), Color(0xFF6dd5ed))
                ),
            ) ){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Language Setting
                    SettingOption(
                        title = "Language",
                        iconRes = R.drawable.langauge,
                        options = listOf("Arabic", "English"),
                        selectedOption = selectedLanguage
                    ) {
                        viewModel.updateLanguage(it)
                    }

                    // Temperature Unit Setting
                    SettingOption(
                        title = "Temperature Unit",
                        iconRes = R.drawable.tempreture,
                        options = listOf("Celsius", "Fahrenheit", "Kelvin"),
                        selectedOption = selectedTemperature
                    ) {
                        viewModel.updateTemperature(it)
                    }

                    // Location Setting
                    SettingOption(
                        title = "Location",
                        iconRes = R.drawable.location,
                        options = listOf("GPS", "Map"),
                        selectedOption = selectedLocation
                    ) {
                        viewModel.updateLocation(it)
                    }

                    // Wind Speed Setting
                    SettingOption(
                        title = "Wind Speed",
                        iconRes = R.drawable.wind,
                        options = listOf("Meter/sec", "Mile/hour"),
                        selectedOption = selectedWindSpeed
                    ) {
                        viewModel.updateWindSpeed(it)
                    }
                }
            }
}

@Composable
fun SettingOption(
    title: String,
    iconRes: Int,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(10.dp, shape = RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2D3E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Color(0xFF64B5F6),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))

                Text(text = title, color = Color.White, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                options.forEach { option ->
                    ChoiceChip(
                        text = option,
                        isSelected = option == selectedOption,
                        onClick = { onOptionSelected(option) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChoiceChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ElevatedFilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text,
                color = if (isSelected) Color.White else Color.LightGray,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        colors = FilterChipDefaults.elevatedFilterChipColors(
            containerColor = if (isSelected) Color(0xFF64B5F6) else Color.Transparent,
            selectedContainerColor = Color(0xFF1E88E5)
        )
    )
}