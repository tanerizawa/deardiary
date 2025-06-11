package com.example.diarydepresiku.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Skema warna pastel lembut (diambil dari Color.kt yang telah disesuaikan)
private val LightColorScheme = lightColorScheme(
    primary = Blue80,             // warna utama aplikasi
    secondary = Green80,          // pendukung seperti tombol sekunder
    background = Gray80,          // latar belakang aplikasi
    surface = Color.White,        // permukaan elemen UI
    error = RedSoft,              // warna untuk pesan error
    onPrimary = TextOnPrimary,    // warna teks di atas primary
    onSecondary = TextOnPrimary,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue40,
    secondary = Green40,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = RedSoft,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black
)

@Composable
fun DiarydepresikuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
