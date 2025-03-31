package com.youssef.weatherforcast.WeatherAlert

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.youssef.weatherforcast.Home.HomeViewModel
import com.youssef.weatherforcast.Model.ForecastResponse
import com.youssef.weatherforcast.Model.WeatherResponse
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AlertScreenMain(
    weatherResponse: WeatherResponse,
    units: String,
    homeViewModel: HomeViewModel
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Convert temperature using HomeViewModel's logic
    val convertedTemp = homeViewModel.convertTemperature(weatherResponse.main.temp, units)
    val formattedTemp = homeViewModel.formatTemperature(convertedTemp)
    val unitSymbol = when (units) {
        "Celsius" -> "C"
        "Fahrenheit" -> "F"
        "Kelvin" -> "K"
        else -> "C"
    }
    val temperatureText = "$formattedTemp°$unitSymbol"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Alarm Manager",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(16.dp),
                onClick = { showDatePicker = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Alarm")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Set an Alarm",
                fontSize = 22.sp,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { showDatePicker = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(text = "Choose Time")
            }
        }

        if (showDatePicker) {
            DateAndTimePickerExample(
                onDismiss = { showDatePicker = false },
                context = context,
                temperature = temperatureText
            )
        }
    }
}

@SuppressLint("ScheduleExactAlarm")
private fun setAlarm(context: Context, time: Long, temperature: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, MyAlarm::class.java).apply {
        putExtra("TEMP_VALUE", temperature)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        0,
        intent,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        else
            PendingIntent.FLAG_UPDATE_CURRENT
    )
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateAndTimePickerExample(
    onDismiss: () -> Unit,
    context: Context,
    temperature: String
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }

    val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    val formattedTime = selectedTime.format(DateTimeFormatter.ofPattern("hh:mm a"))

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = { },
        title = { Text("Pick Date & Time") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Selected Date: $formattedDate", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Selected Time: $formattedTime", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { showTimePicker = true }) {
                    Text(text = "Select Time")
                }

                if (showTimePicker) {
                    val timePickerState = rememberTimePickerState(
                        initialHour = selectedTime.hour,
                        initialMinute = selectedTime.minute
                    )

                    AlertDialog(
                        onDismissRequest = { showTimePicker = false },
                        confirmButton = {
                            Button(onClick = {
                                selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                                showTimePicker = false

                                val triggerTimeMillis = convertToMillis(selectedDate, selectedTime)
                                setAlarm(context, triggerTimeMillis, temperature)

                                onDismiss()
                            }) {
                                Text("OK")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showTimePicker = false }) {
                                Text("Cancel")
                            }
                        },
                        title = { Text("Select Time") },
                        text = { TimePicker(state = timePickerState) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { onDismiss() }) {
                    Text("Close")
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun convertToMillis(date: LocalDate, time: LocalTime): Long {
    val zoneId = java.time.ZoneId.systemDefault()
    return date.atTime(time).atZone(zoneId).toInstant().toEpochMilli()
}