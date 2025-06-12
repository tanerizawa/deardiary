package com.example.diarydepresiku.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.diarydepresiku.ReminderPreferences
import com.example.diarydepresiku.ui.theme.*

@Composable
fun SettingsScreen(
    prefs: ReminderPreferences,
    modifier: Modifier = Modifier
) {
    var reminderEnabled by remember { mutableStateOf(false) }
    var reminderTime by remember { mutableStateOf("20:00") }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

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
    ) {
        // Header
        SettingsHeader()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profile Section
            item {
                ProfileSection()
            }

            // Preferences Section
            item {
                SectionHeader(title = "Preferensi")
            }

            item {
                SettingsCard {
                    Column {
                        ToggleSettingItem(
                            icon = Icons.Filled.Notifications,
                            title = "Pengingat Harian",
                            description = "Dapatkan pengingat untuk menulis diary",
                            checked = reminderEnabled,
                            onCheckedChange = { reminderEnabled = it },
                            iconColor = PeachDark
                        )

                        if (reminderEnabled) {
                            Divider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Gray200
                            )
                            
                            TimeSettingItem(
                                title = "Waktu Pengingat",
                                time = reminderTime,
                                onTimeChange = { reminderTime = it }
                            )
                        }
                    }
                }
            }

            item {
                SettingsCard {
                    Column {
                        ToggleSettingItem(
                            icon = Icons.Filled.NotificationsActive,
                            title = "Notifikasi",
                            description = "Terima notifikasi dari aplikasi",
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
                            iconColor = MintDark
                        )

                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Gray200
                        )

                        ToggleSettingItem(
                            icon = Icons.Filled.DarkMode,
                            title = "Mode Gelap",
                            description = "Gunakan tema gelap untuk aplikasi",
                            checked = darkModeEnabled,
                            onCheckedChange = { darkModeEnabled = it },
                            iconColor = SoftBlueDark
                        )
                    }
                }
            }

            // Privacy & Security Section
            item {
                SectionHeader(title = "Privasi & Keamanan")
            }

            item {
                SettingsCard {
                    Column {
                        ClickableSettingItem(
                            icon = Icons.Filled.Lock,
                            title = "Keamanan Data",
                            description = "Atur keamanan dan privasi data",
                            iconColor = Error,
                            onClick = { /* Handle security settings */ }
                        )

                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Gray200
                        )

                        ClickableSettingItem(
                            icon = Icons.Filled.Backup,
                            title = "Backup & Restore",
                            description = "Cadangkan atau pulihkan data diary",
                            iconColor = Warning,
                            onClick = { /* Handle backup */ }
                        )

                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Gray200
                        )

                        ClickableSettingItem(
                            icon = Icons.Filled.Delete,
                            title = "Hapus Semua Data",
                            description = "Hapus permanen semua catatan diary",
                            iconColor = Error,
                            onClick = { /* Handle delete all data */ }
                        )
                    }
                }
            }

            // Support Section
            item {
                SectionHeader(title = "Dukungan")
            }

            item {
                SettingsCard {
                    Column {
                        ClickableSettingItem(
                            icon = Icons.Filled.Help,
                            title = "Bantuan & FAQ",
                            description = "Temukan jawaban untuk pertanyaan umum",
                            iconColor = SoftBlueDark,
                            onClick = { /* Handle help */ }
                        )

                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Gray200
                        )

                        ClickableSettingItem(
                            icon = Icons.Filled.Feedback,
                            title = "Kirim Masukan",
                            description = "Bantu kami meningkatkan aplikasi",
                            iconColor = MintDark,
                            onClick = { /* Handle feedback */ }
                        )

                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Gray200
                        )

                        ClickableSettingItem(
                            icon = Icons.Filled.Info,
                            title = "Tentang Aplikasi",
                            description = "Versi 1.0.0 • Build 2024.1",
                            iconColor = PeachDark,
                            onClick = { /* Handle about */ }
                        )
                    }
                }
            }

            // Mental Health Resources
            item {
                SectionHeader(title = "Sumber Bantuan")
            }

            item {
                SettingsCard {
                    Column {
                        EmergencyContactCard()

                        Divider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Gray200
                        )

                        ClickableSettingItem(
                            icon = Icons.Filled.Psychology,
                            title = "Layanan Konseling",
                            description = "Temukan bantuan profesional terdekat",
                            iconColor = MoodVeryHappy,
                            onClick = { /* Handle counseling services */ }
                        )
                    }
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SettingsHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pengaturan",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "Personalisasi pengalaman Anda",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProfileSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile avatar
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(SoftBlueDark, MintDark)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "😊",
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Pengguna Diary",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "Bergabung sejak ${getCurrentDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row {
                    StatChip(label = "Catatan", value = "12")
                    Spacer(modifier = Modifier.width(8.dp))
                    StatChip(label = "Hari", value = "7")
                }
            }
        }
    }
}

@Composable
private fun StatChip(
    label: String,
    value: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = SoftBlueLight
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = SoftBlueDark,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun SectionHeader(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.padding(20.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun ToggleSettingItem(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    iconColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = iconColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = iconColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Gray200
            )
        )
    }
}

@Composable
private fun TimeSettingItem(
    title: String,
    time: String,
    onTimeChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp)) // Spacer to align with other items

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        }

        OutlinedButton(
            onClick = { /* Show time picker */ },
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = SoftBlueDark
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun ClickableSettingItem(
    icon: ImageVector,
    title: String,
    description: String,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = iconColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 16.sp
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Gray400,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun EmergencyContactCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = ErrorLight
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Emergency,
                    contentDescription = null,
                    tint = Error,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Butuh Bantuan Segera?",
                    style = MaterialTheme.typography.titleSmall,
                    color = Error,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Jika Anda dalam kondisi darurat atau membutuhkan bantuan segera:",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 16.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EmergencyButton(
                    text = "Hotline 119",
                    onClick = { /* Call 119 */ }
                )
                EmergencyButton(
                    text = "Crisis Center",
                    onClick = { /* Call crisis center */ }
                )
            }
        }
    }
}

@Composable
private fun EmergencyButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Error
        ),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = Brush.horizontalGradient(
                colors = listOf(Error, Error)
            )
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun getCurrentDate(): String {
    return "Januari 2024" // Placeholder
}
