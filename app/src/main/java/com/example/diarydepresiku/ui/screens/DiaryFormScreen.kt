package com.example.diarydepresiku.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.diarydepresiku.DiaryViewModel
import com.example.diarydepresiku.ui.components.MoodSelector
import com.example.diarydepresiku.ui.components.MoodSummaryWidget
import com.example.diarydepresiku.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryFormScreen(
    viewModel: DiaryViewModel,
    onNavigateToContent: () -> Unit,
    modifier: Modifier = Modifier
) {
    var content by remember { mutableStateOf("") }
    var selectedMood by remember { mutableStateOf<String?>(null) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val scrollState = rememberScrollState()

    // Observe mood data for summary widget
    val moodCounts by viewModel.moodCounts.collectAsState()
    val allEntries by viewModel.allEntries.collectAsState()

    // Success message animation
    LaunchedEffect(showSuccessMessage) {
        if (showSuccessMessage) {
            delay(2000)
            showSuccessMessage = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundGradientStart,
                        BackgroundGradientEnd
                    )
                )
            )
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header with date
        DiaryHeader()

        Spacer(modifier = Modifier.height(24.dp))

        // Mood Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                MoodSelector(
                    selectedMood = selectedMood,
                    onMoodSelected = { selectedMood = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Content Input
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Ceritakan tentang hari Anda",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    placeholder = {
                        Text(
                            text = "Tulis tentang perasaan, kejadian, atau apapun yang ingin Anda ungkapkan...",
                            color = TextHint,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SoftBlueDark,
                        unfocusedBorderColor = Gray200,
                        focusedLabelColor = SoftBlueDark,
                        unfocusedLabelColor = TextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Save Button
        Button(
            onClick = {
                if (content.isNotBlank() && selectedMood != null) {
                    isLoading = true
                    keyboardController?.hide()

                    viewModel.saveDiaryEntry(
                        content = content,
                        mood = selectedMood!!
                    )

                    // Reset form
                    content = ""
                    selectedMood = null
                    isLoading = false
                    showSuccessMessage = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = content.isNotBlank() && selectedMood != null && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (content.isNotBlank() && selectedMood != null)
                    SoftBlueDark else Gray200,
                contentColor = if (content.isNotBlank() && selectedMood != null)
                    Color.White else TextHint
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Simpan Catatan",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Success Message
        AnimatedVisibility(
            visible = showSuccessMessage,
            enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(300)
            ),
            exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(300)
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SuccessLight
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Success,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Catatan berhasil disimpan! ✨",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Success,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Mood Summary Widget
        if (allEntries.isNotEmpty()) {
            MoodSummaryWidget(
                moodCounts = moodCounts,
                totalEntries = allEntries.size
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Tips card
        TipsCard()
    }
}

@Composable
private fun DiaryHeader() {
    val currentDate = LocalDateTime.now()
    val dayFormatter = DateTimeFormatter.ofPattern("EEEE", Locale("id", "ID"))
    val dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Diary Harian",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = null,
                tint = SoftBlueDark,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${dayFormatter.format(currentDate)}, ${dateFormatter.format(currentDate)}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun TipsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = PeachLight
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "💡 Tips Menulis Diary",
                style = MaterialTheme.typography.titleSmall,
                color = PeachDark,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "• Tulis dengan jujur tentang perasaan Anda\n" +
                      "• Tidak perlu sempurna, yang penting ekspresif\n" +
                      "• Ceritakan hal-hal kecil yang membuat bahagia\n" +
                      "• Gunakan diary sebagai refleksi diri",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
