package com.hritwik.sassyskies.widget

import androidx.glance.appwidget.GlanceAppWidgetReceiver

class ComposeWeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = WeatherWidget()
}