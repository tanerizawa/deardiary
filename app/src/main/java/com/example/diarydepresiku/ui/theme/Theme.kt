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
import com.example.diarydepresiku.ui.theme.scaledTypography

// Skema warna pastel lembut (diambil dari Color.kt yang telah disesuaikan)
// Color scheme using the refreshed palette defined in Color.kt
private val LightColorScheme = lightColorScheme(
    primary = Blue80,             // soft blue
    secondary = Green80,          // mint green
    tertiary = SoftYellow,        // accent/warning color
    background = Gray80,          // light gray background
    surface = Color.White,
    error = RedSoft,
    onPrimary = TextOnPrimary,    // warna teks di atas primary
    onSecondary = TextOnPrimary,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onTertiary = Color.Black,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue40,
    secondary = Green40,
    tertiary = SoftYellow,
    background = Gray40,
    surface = Gray40,
    error = RedSoft,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onTertiary = Color.Black,
    onError = Color.Black
)

@Composable
fun DiarydepresikuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    fontScale: Float = 1f,
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
        typography = scaledTypography(fontScale),
        content = content
    )
}
