package com.youssef.weatherforcast.WeatherAlert

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.youssef.weatherforcast.R
import java.util.Calendar

@Composable
fun WeatherAlertScreen(viewModel: WeatherAlertsViewModel = viewModel()) {
    val context = LocalContext.current
    val viewModel: WeatherAlertsViewModel = viewModel(
        factory = WeatherAlertsViewModelFactory(context)
    )
        var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf("Alarm") }
    var alertId by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A1E)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.alert),
            contentDescription = "Warning Icon",
            modifier = Modifier.size(100.dp)
        )

        Text(
            text = "You haven't any alarms",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A3A))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TimePickerField(label = "Start Time", value = startTime) { startTime = it }
                Spacer(modifier = Modifier.height(12.dp))
                TimePickerField(label = "End Time", value = endTime) { endTime = it }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Notify me by", color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    NotificationOption("Alarm", selectedOption) { selectedOption = it }
                    Spacer(modifier = Modifier.width(16.dp))
                    NotificationOption("Notification", selectedOption) { selectedOption = it }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    val newAlertId = System.currentTimeMillis().toInt()
                    alertId = newAlertId
                    val alert = WeatherAlert(
                        id = newAlertId,
                        type = when (selectedOption) {
                            "Alarm" -> AlertType.ALARM_SOUND
                            else -> AlertType.NOTIFICATION
                        },
                        message = "Weather Alert",
                        startTime = startTime,
                        endTime = endTime,
                        timestamp = System.currentTimeMillis()
                    )
                    viewModel.scheduleAlert(alert)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                modifier = Modifier.weight(1f)
            ) {
                Text("SAVE", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    alertId?.let { viewModel.cancelAlert(it) }
                    // Clear all fields and reset
                    startTime = ""
                    endTime = ""
                    selectedOption = "Alarm"
                    alertId = null
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier.weight(1f)
            ) {
                Text("CANCEL", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun TimePickerField(label: String, value: String, onTimeSelected: (String) -> Unit) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showTimePicker(context, onTimeSelected) }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label, color = Color.Black) },
            leadingIcon = {
                Icon(Icons.Filled.Check, contentDescription = "Time Picker", tint = Color.Black)
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Black,
                focusedIndicatorColor = Color.Black,
                unfocusedIndicatorColor = Color.Black
            ),
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun NotificationOption(option: String, selectedOption: String, onOptionSelected: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onOptionSelected(option) }
    ) {
        RadioButton(
            selected = selectedOption == option,
            onClick = { onOptionSelected(option) },
            colors = RadioButtonDefaults.colors(selectedColor = Color.Cyan)
        )
        Text(option, color = Color.White)
    }
}

fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            onTimeSelected(formattedTime)
        },
        hour,
        minute,
        true
    ).show()
}

@Preview(showBackground = true)
@Composable
fun PreviewAlarmScreen() {
    WeatherAlertScreen()
}