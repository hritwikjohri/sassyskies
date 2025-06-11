package com.hritwik.sassyskies.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.unit.sp
import com.hritwik.sassyskies.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val JosefinSans = FontFamily(
    Font(
        googleFont = GoogleFont("Josefin Sans"),
        fontProvider = provider,
    )
)

val bodyFontFamily = JosefinSans
val displayFontFamily = JosefinSans

// Default Material 3 typography values
val baseline = Typography()

val AppTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
    bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily),
)

// Custom Weather Typography for special text elements
object WeatherTypography {
    val MainTemperature = TextStyle(
        fontFamily = JosefinSans,
        fontWeight = FontWeight.Light,
        fontSize = 128.sp,
        lineHeight = 120.sp,
        letterSpacing = (-8).sp
    )

    val SarcasticMessage = TextStyle(
        fontFamily = JosefinSans,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 36.sp,
        letterSpacing = (-1).sp
    )

    val AppTitle = TextStyle(
        fontFamily = JosefinSans,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 64.sp,
        lineHeight = 55.sp,
        letterSpacing = (-4).sp
    )

    val LocationName = TextStyle(
        fontFamily = JosefinSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        letterSpacing = 0.sp
    )
}