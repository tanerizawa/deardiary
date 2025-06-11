package com.example.diarydepresiku.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import com.example.diarydepresiku.R
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.ExperimentalTextApi // <<< Pastikan import ini ada!

@OptIn(ExperimentalTextApi::class) // <<< Anotasi ini di AppFontFamily
private val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val AppFontFamily = FontFamily(
    Font(GoogleFont("Roboto"), fontProvider, FontWeight.Normal),
    Font(GoogleFont("Roboto"), fontProvider, FontWeight.Medium),
    Font(GoogleFont("Roboto"), fontProvider, FontWeight.SemiBold),
    Font(GoogleFont("Roboto"), fontProvider, FontWeight.Bold)
)


@OptIn(ExperimentalTextApi::class) // <<< Anotasi ini juga di Typography
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp
    ),
    titleLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    )
)
