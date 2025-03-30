package com.youssef.weatherforcast.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.youssef.weatherforcast.WeatherAlert.*

@Composable
fun WeatherAlertScreen() {
    val context = LocalContext.current
    val viewModelFactory = WeatherAlertsViewModelFactory(context)
    val viewModel: WeatherAlertsViewModel = viewModel(factory = viewModelFactory)

    var selectedDuration by remember { mutableStateOf(60000L) } // 1 دقيقة
    var selectedAlertType by remember { mutableStateOf(AlertType.NOTIFICATION) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Set Weather Alert", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // اختيار مدة التنبيه
        OutlinedTextField(
            value = (selectedDuration / 60000).toString(),
            onValueChange = { input ->
                selectedDuration = input.toLongOrNull()?.times(60000) ?: selectedDuration
            },
            label = { Text("Duration (Minutes)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // اختيار نوع التنبيه
        Row {
            RadioButton(
                selected = selectedAlertType == AlertType.NOTIFICATION,
                onClick = { selectedAlertType = AlertType.NOTIFICATION }
            )
            Text("Notification")

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = selectedAlertType == AlertType.ALARM_SOUND,
                onClick = { selectedAlertType = AlertType.ALARM_SOUND }
            )
            Text("Alarm")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // زر جدولة التنبيه
        Button(
            onClick = {
                viewModel.scheduleAlert(selectedDuration, selectedAlertType)
                Toast.makeText(context, "Alert Scheduled!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Schedule Alert")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.cancelAlert(viewModel.currentAlertId)
                Toast.makeText(context, "Alert Canceled!", Toast.LENGTH_SHORT).show()
            },
            colors = ButtonDefaults.buttonColors(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel Alert")
        }
    }
}
