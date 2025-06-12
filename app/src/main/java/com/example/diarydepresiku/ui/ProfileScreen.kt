package com.example.diarydepresiku.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.diarydepresiku.DiaryViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    viewModel: DiaryViewModel,
    onNavigateToSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val achievements by viewModel.achievements.collectAsState()
    val streak by viewModel.entryStreak.collectAsState()

    val context = LocalContext.current

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

        Spacer(Modifier.padding(top = 24.dp))
        Text(
            text = "Jika Anda merasa membutuhkan bantuan profesional, Anda dapat menghubungi layanan konseling.",
            style = MaterialTheme.typography.bodyMedium
        )
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:119"))
                context.startActivity(intent)
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Hubungi Bantuan")
        }
    }
}
