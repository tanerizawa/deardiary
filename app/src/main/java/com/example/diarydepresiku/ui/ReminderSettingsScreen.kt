package com.example.diarydepresiku.ui

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.diarydepresiku.ReminderPreferences
import com.example.diarydepresiku.cancelDailyReminder
import com.example.diarydepresiku.scheduleDailyReminder
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ReminderSettingsScreen(
    prefs: ReminderPreferences,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val enabled by prefs.reminderEnabled.collectAsState(initial = false)
    val time by prefs.reminderTime.collectAsState(initial = LocalTime.of(8, 0))
    var showPicker by remember { mutableStateOf(false) }

    if (showPicker) {
        TimePickerDialog(
            context,
            { _, hour: Int, minute: Int ->
                val selected = LocalTime.of(hour, minute)
                coroutineScope.launch { prefs.setReminderTime(selected) }
                if (enabled) scheduleDailyReminder(context, selected)
                showPicker = false
            },
            time.hour,
            time.minute,
            true
        ).apply { setOnDismissListener { showPicker = false } }.show()
    }

    Column(modifier = modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Enable Daily Reminder",
                modifier = Modifier.weight(1f)
            )
            Switch(checked = enabled, onCheckedChange = { value ->
                coroutineScope.launch { prefs.setReminderEnabled(value) }
                if (value) {
                    scheduleDailyReminder(context, time)
                } else {
                    cancelDailyReminder(context)
                }
            })
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { showPicker = true }, enabled = enabled) {
            val formatted = time.format(DateTimeFormatter.ofPattern("HH:mm"))
            Text("Reminder Time: $formatted")
        }
    }
}
