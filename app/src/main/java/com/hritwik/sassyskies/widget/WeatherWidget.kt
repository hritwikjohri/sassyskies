package com.hritwik.sassyskies.widget

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.hritwik.sassyskies.widget.ui.WeatherWidgetContent

class WeatherWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val weatherData = WidgetDataManager.getCachedWeatherData(context)
        if (weatherData == null) {
            val workRequest = OneTimeWorkRequestBuilder<ComposeWidgetUpdateWorker>().build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }

        provideContent {
            GlanceTheme {
                WeatherWidgetContent(weatherData = weatherData)
            }
        }
    }
}