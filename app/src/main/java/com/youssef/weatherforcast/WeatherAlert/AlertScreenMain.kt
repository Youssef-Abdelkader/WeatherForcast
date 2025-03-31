package com.youssef.weatherforcast.WeatherAlert

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.foundation.background

import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.youssef.weatherforcast.Home.HomeViewModel
import com.youssef.weatherforcast.Model.WeatherResponse
import com.youssef.weatherforcast.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "ScheduleExactAlarm")
@Composable
fun AlertScreenMain(
    weatherResponse: WeatherResponse,
    units: String,
    homeViewModel: HomeViewModel
) {
    val context = LocalContext.current
    val repo = (context.applicationContext as WeatherApplication).repo
    val alerts by repo.getAllAlerts().collectAsState(emptyList())
    var showDatePicker by remember { mutableStateOf(false) }

    val convertedTemp = homeViewModel.convertTemperature(weatherResponse.main.temp, units)
    val formattedTemp = homeViewModel.formatTemperature(convertedTemp)
    val unitSymbol = when (units) {
        "Celsius" -> "°C"
        "Fahrenheit" -> "°F"
        "Kelvin" -> "K"
        else -> "°C"
    }
    val temperatureText = "$formattedTemp$unitSymbol"

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
                .padding(padding)
        ) {
            if (alerts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No alarms set yet!")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    items(alerts) { alert ->
                        AlertItem(
                            alert = alert,
                            onRemove = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    repo.deleteAlert(alert)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
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

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun AlertItem(
    alert: WeatherAlert,
    onRemove: () -> Unit
) {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val startDate = sdf.parse(alert.startTime)
    val endDate = sdf.parse(alert.endTime)

    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    val startTime = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
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
                Icon(
                    painter = painterResource(id = R.drawable.alert),
                    contentDescription = "Alarm Icon",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Alert Set For",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${startTime.format(dateFormatter)} - ${startTime.format(timeFormatter)}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))


                }

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color(0xFFFF5252), shape = RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove Alert",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

// Keep all other existing functions (DateAndTimePickerExample, setAlarm, convertToMillis) unchanged


@SuppressLint("ScheduleExactAlarm")
private fun setAlarm(context: Context, time: Long, temperature: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, MyAlarm::class.java).apply {
        putExtra("TEMP_VALUE", temperature)
    }

    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    } else {
        PendingIntent.FLAG_UPDATE_CURRENT
    }

    val pendingIntent = PendingIntent.getBroadcast(context, Random().nextInt(100000), intent, flags)

    // Use most precise alarm method available
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time,
                pendingIntent
            )
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        }
        else -> {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent)
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateAndTimePickerExample(
    onDismiss: () -> Unit,
    context: Context,
    temperature: String
) {
    val repo = (context.applicationContext as WeatherApplication).repo
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

                                // Create and save the alert
                                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                                val startTime = selectedDate.atTime(selectedTime).format(formatter)
                                val endTime = startTime // Same as start time for single alert

                                val weatherAlert = WeatherAlert(
                                    startTime = startTime,
                                    endTime = endTime,
                                    type = AlertType.ALARM,
                                    message = "Alert set for $temperature",
                                    timestamp = System.currentTimeMillis()
                                )

                                CoroutineScope(Dispatchers.IO).launch {
                                    repo.insertAlert(weatherAlert)
                                }

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
/////////////////////////////