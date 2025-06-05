package com.hritwik.sassyskies.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextDefaults
import androidx.glance.text.TextStyle
import com.hritwik.sassyskies.model.WidgetWeatherData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun WeatherDataContent(weatherData: WidgetWeatherData) {
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

        // Location
        Text(
            text = weatherData.location,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextDefaults.defaultTextColor
            )
        )

        Spacer(modifier = GlanceModifier.height(12.dp))

        // Temperature
        Text(
            text = "${weatherData.temperature}°",
            style = TextStyle(
                fontSize = 36.sp,
                fontWeight = FontWeight.Normal,
                color = TextDefaults.defaultTextColor
            )
        )

        Spacer(modifier = GlanceModifier.height(12.dp))

        // Sarcastic Message (truncated for widget)
        val shortMessage = if (weatherData.sarcasticMessage.length > 80) {
            weatherData.sarcasticMessage.take(77) + "..."
        } else {
            weatherData.sarcasticMessage
        }

        Text(
            text = shortMessage,
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextDefaults.defaultTextColor
            ),
            maxLines = 2
        )
    }
}