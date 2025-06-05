package com.hritwik.sassyskies.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import androidx.glance.text.TextStyle
import com.hritwik.sassyskies.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun LoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = GlanceModifier.fillMaxWidth()
    ) {
        // Time Display
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        Text(
            text = timeFormat.format(Date()),
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextDefaults.defaultTextColor
            )
        )

        Spacer(modifier = GlanceModifier.height(8.dp))

        Text(
            text = "Sassy Skies",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDefaults.defaultTextColor
            )
        )

        Spacer(modifier = GlanceModifier.height(12.dp))

        Text(
            text = "--°",
            style = TextStyle(
                fontSize = 36.sp,
                fontWeight = FontWeight.Normal,
                color = TextDefaults.defaultTextColor
            )
        )

        Spacer(modifier = GlanceModifier.height(12.dp))

        Text(
            text = "Getting weather data...",
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextDefaults.defaultTextColor
            )
        )
    }
}

private fun getWeatherIconResource(weatherCondition: String): Int {
    return when (weatherCondition.lowercase()) {
        "clear", "sunny" -> R.drawable.ic_weather_sunny
        "clouds", "cloudy" -> R.drawable.ic_weather_cloudy
        "rain", "drizzle" -> R.drawable.ic_weather_rainy
        "snow" -> R.drawable.ic_weather_snowy
        "thunderstorm", "storm" -> R.drawable.ic_weather_stormy
        "fog", "mist", "haze" -> R.drawable.ic_weather_foggy
        "wind" -> R.drawable.ic_weather_windy
        else -> R.drawable.ic_weather_default
    }
}