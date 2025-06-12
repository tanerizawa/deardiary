package com.example.diarydepresiku.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
// Hapus ExperimentalTextApi jika tidak lagi menggunakan googleFont atau fitur eksperimental teks lainnya
// Jika Anda tidak menggunakan fitur teks eksperimental lain, Anda bisa menghapus @OptIn(ExperimentalTextApi::class)
// Tapi jika ada bagian lain yang memerlukan, biarkan saja.
// Biasanya dengan font lokal, anotasi ini tidak lagi diperlukan kecuali ada fitur TextStyle yang memakainya.

import com.example.diarydepresiku.R // Pastikan import R ini ada dan benar!

// Tidak perlu @OptIn(ExperimentalTextApi::class) lagi di sini jika hanya menggunakan Font(R.font.xxx)
val AppFontFamily = FontFamily(
    // Asumsikan Anda sudah mengunduh dan menamai file-file font seperti ini:
    // app/src/main/res/font/roboto_regular.ttf
    // app/src/main/res/font/roboto_medium.ttf
    // app/src/main/res/font/roboto_semibold.ttf (jika ada, atau gunakan bold)
    // app/src/main/res/font/roboto_bold.ttf

    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_semibold, FontWeight.SemiBold), // Sesuaikan jika tidak ada semibold
    Font(R.font.roboto_bold, FontWeight.Bold)
)

// Bagian Typography tetap sama, karena ia hanya mereferensikan AppFontFamily
// @OptIn(ExperimentalTextApi::class) // Hapus ini juga jika tidak ada TextStyle yang membutuhkannya
val AppTypography = Typography(
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

fun scaledTypography(scale: Float): Typography {
    if (scale == 1f) return AppTypography

    fun TextStyle.scale() = copy(fontSize = (fontSize.value * scale).sp)

    return Typography(
        displayLarge = AppTypography.displayLarge.scale(),
        titleLarge = AppTypography.titleLarge.scale(),
        titleMedium = AppTypography.titleMedium.scale(),
        bodyLarge = AppTypography.bodyLarge.scale(),
        bodyMedium = AppTypography.bodyMedium.scale(),
        labelLarge = AppTypography.labelLarge.scale()
    )
}
