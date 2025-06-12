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

// Light theme color scheme - Mental Health focused
private val LightColorScheme = lightColorScheme(
    primary = SoftBlueDark,                 // Calming blue for primary elements
    onPrimary = Color.White,                // White text on primary
    primaryContainer = SoftBlueLight,       // Very light blue for containers
    onPrimaryContainer = TextPrimary,       // Dark text on light containers

    secondary = MintDark,                   // Mint green for secondary elements
    onSecondary = Color.White,              // White text on secondary
    secondaryContainer = MintLight,         // Very light mint for containers
    onSecondaryContainer = TextPrimary,     // Dark text on light containers

    tertiary = PeachDark,                   // Warm accent color
    onTertiary = Color.White,               // White text on tertiary
    tertiaryContainer = PeachLight,         // Light peach for containers
    onTertiaryContainer = TextPrimary,      // Dark text on light containers

    error = Error,                          // Soft red for errors
    onError = Color.White,                  // White text on error
    errorContainer = ErrorLight,            // Light red for error containers
    onErrorContainer = TextPrimary,         // Dark text on error containers

    background = Gray50,                    // Very light background
    onBackground = TextPrimary,             // Dark text on background
    surface = Color.White,                  // White surfaces
    onSurface = TextPrimary,                // Dark text on surfaces
    surfaceVariant = SurfaceVariant,        // Slightly tinted surface
    onSurfaceVariant = TextSecondary,       // Medium gray text

    outline = Gray400,                      // Medium gray for outlines
    outlineVariant = Gray200,               // Light gray for subtle outlines
    scrim = Color.Black.copy(alpha = 0.32f), // Semi-transparent overlay
    inverseSurface = Gray800,               // Dark surface for inverse
    inverseOnSurface = Color.White,         // Light text on inverse surface
    inversePrimary = SoftBlue               // Light primary for inverse
)

// Dark theme color scheme - Soothing dark variant
private val DarkColorScheme = darkColorScheme(
    primary = SoftBlue,                     // Lighter blue for dark theme
    onPrimary = Gray800,                    // Dark text on primary
    primaryContainer = SoftBlueDark,        // Darker blue for containers
    onPrimaryContainer = Color.White,       // White text on dark containers

    secondary = MintGreen,                  // Lighter mint for dark theme
    onSecondary = Gray800,                  // Dark text on secondary
    secondaryContainer = MintDark,          // Darker mint for containers
    onSecondaryContainer = Color.White,     // White text on dark containers

    tertiary = Peach,                       // Lighter peach for dark theme
    onTertiary = Gray800,                   // Dark text on tertiary
    tertiaryContainer = PeachDark,          // Darker peach for containers
    onTertiaryContainer = Color.White,      // White text on dark containers

    error = Error,                          // Same error color
    onError = Color.White,                  // White text on error
    errorContainer = Color(0xFF93000A),     // Dark red for error containers
    onErrorContainer = ErrorLight,          // Light red text on dark error

    background = Color(0xFF121212),         // Dark background
    onBackground = Color.White,             // White text on background
    surface = Color(0xFF1E1E1E),            // Dark surface
    onSurface = Color.White,                // White text on surface
    surfaceVariant = Color(0xFF2C2C2C),     // Darker surface variant
    onSurfaceVariant = Gray400,             // Light gray text

    outline = Gray600,                      // Medium gray for outlines
    outlineVariant = Gray800,               // Dark gray for subtle outlines
    scrim = Color.Black.copy(alpha = 0.6f), // More opaque overlay for dark
    inverseSurface = Gray100,               // Light surface for inverse
    inverseOnSurface = TextPrimary,         // Dark text on inverse surface
    inversePrimary = SoftBlueDark           // Dark primary for inverse
)

@Composable
fun DiarydepresikuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled for consistent mental health theming
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Dynamic color is disabled to maintain consistent calming theme
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
