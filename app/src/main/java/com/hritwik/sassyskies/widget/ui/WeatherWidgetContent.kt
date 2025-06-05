package com.hritwik.sassyskies.widget.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import com.hritwik.sassyskies.MainActivity
import com.hritwik.sassyskies.model.WidgetWeatherData

@Composable
fun WeatherWidgetContent(weatherData: WidgetWeatherData?) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .padding(16.dp)
            .clickable(actionStartActivity<MainActivity>()),
        contentAlignment = Alignment.Center
    ) {
        if (weatherData != null) {
            WeatherDataContent(weatherData = weatherData)
        } else {
            LoadingContent()
        }
    }
}