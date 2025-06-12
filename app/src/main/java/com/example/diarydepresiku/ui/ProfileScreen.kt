package com.example.diarydepresiku.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.diarydepresiku.DiaryViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    viewModel: DiaryViewModel,
    onNavigateToSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val achievements by viewModel.achievements.collectAsState()
    val streak by viewModel.entryStreak.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Profile", style = MaterialTheme.typography.titleLarge)
        Text(text = "Streak: $streak days", style = MaterialTheme.typography.bodyLarge)
        if (achievements.isNotEmpty()) {
            Text(text = "Badges:", style = MaterialTheme.typography.titleMedium)
            achievements.forEach { badge ->
                Text(text = badge.name)
            }
        }
        onNavigateToSettings?.let {
            Button(onClick = it, modifier = Modifier.padding(top = 16.dp)) {
                Text("Settings")
            }
        }
    }
}
