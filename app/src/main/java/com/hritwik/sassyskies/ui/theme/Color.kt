package com.hritwik.sassyskies.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val ColorScheme.warningContainer: Color
    get() = if (this == androidx.compose.material3.lightColorScheme()) {
        Color(0xFFFFF4E6) // Light orange
    } else {
        Color(0xFF2D1B00) // Dark orange
    }

val ColorScheme.onWarningContainer: Color
    get() = if (this == androidx.compose.material3.lightColorScheme()) {
        Color(0xFF8B4513) // Dark orange text
    } else {
        Color(0xFFFFE0B3) // Light orange text
    }