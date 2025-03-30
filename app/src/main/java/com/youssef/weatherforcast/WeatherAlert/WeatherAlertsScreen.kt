import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
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
import java.util.Calendar
import com.youssef.weatherforcast.R

@Composable
fun WeatherAlertScreen() {
    val context = LocalContext.current
    var startDuration by remember { mutableStateOf("") }
    var endDuration by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf("Alarm") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A1E)), // Dark background
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Warning icon
        Image(
            painter = painterResource(id = R.drawable.alert),
            contentDescription = "Warning Icon",
            modifier = Modifier.size(100.dp)
        )

        // No alarms text
        Text(
            text = "You haven't any alarms",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        // Card for input fields
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A3A))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Start duration field
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePicker(context) { startDuration = it } }
                ) {
                    OutlinedTextField(
                        value = startDuration,
                        onValueChange = {},
                        label = { Text("Start duration", color = Color.Black) },
                        leadingIcon = {
                            Icon(Icons.Filled.Check, contentDescription = "Start Time", tint = Color.Black)
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,  // لون الخلفية عند التركيز
                            unfocusedContainerColor = Color.White, // لون الخلفية عند عدم التركيز
                            disabledContainerColor = Color.White, // لون الخلفية عند التعطيل
                            focusedTextColor = Color.White, // لون النص عند التركيز
                            unfocusedTextColor = Color.White, // لون النص عند عدم التركيز
                            disabledTextColor = Color.White, // لون النص عند التعطيل
                            focusedIndicatorColor = Color.White, // لون الحد عند التركيز
                            unfocusedIndicatorColor = Color.White // لون الحد عند عدم التركيز
                        ),
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )


                }

                Spacer(modifier = Modifier.height(12.dp))

                // End duration field
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePicker(context) { endDuration = it } }
                ) {
                    OutlinedTextField(
                        value = startDuration,
                        onValueChange = {},
                        label = { Text("Start duration", color = Color.Black) },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = "Start Time",
                                tint = Color.Black
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,  // لون الخلفية عند التركيز
                            unfocusedContainerColor = Color.White, // لون الخلفية عند عدم التركيز
                            disabledContainerColor = Color.White, // لون الخلفية عند التعطيل
                            focusedTextColor = Color.White, // لون النص عند التركيز
                            unfocusedTextColor = Color.White, // لون النص عند عدم التركيز
                            disabledTextColor = Color.White, // لون النص عند التعطيل
                            focusedIndicatorColor = Color.White, // لون الحد عند التركيز
                            unfocusedIndicatorColor = Color.White // لون الحد عند عدم التركيز
                        ),
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notification type selection
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Notify me by", color = Color.White, fontSize = 14.sp)

                    Spacer(modifier = Modifier.width(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { selectedOption = "Alarm" }
                    ) {
                        RadioButton(
                            selected = selectedOption == "Alarm",
                            onClick = { selectedOption = "Alarm" },
                            colors = RadioButtonDefaults.colors(selectedColor = Color.Cyan)
                        )
                        Text("Alarm", color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { selectedOption = "Notification" }
                    ) {
                        RadioButton(
                            selected = selectedOption == "Notification",
                            onClick = { selectedOption = "Notification" },
                            colors = RadioButtonDefaults.colors(selectedColor = Color.Cyan)
                        )
                        Text("Notification", color = Color.White)
                    }
                }
            }
        }

        // Buttons Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Save button
            Button(
                onClick = { /* Save action */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                modifier = Modifier.weight(1f)
            ) {
                Text("SAVE", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Cancel button
            Button(
                onClick = { /* Cancel action */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                modifier = Modifier.weight(1f)
            ) {
                Text("CANCEL", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

// Function to show Time Picker Dialog
fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            onTimeSelected(formattedTime) // تحديث القيمة مباشرةً في الحقل
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
