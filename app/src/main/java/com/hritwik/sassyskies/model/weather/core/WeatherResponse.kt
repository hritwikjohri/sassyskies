package com.hritwik.sassyskies.model.weather.core

import com.hritwik.sassyskies.model.weather.detail.Clouds
import com.hritwik.sassyskies.model.weather.detail.Coord
import com.hritwik.sassyskies.model.weather.detail.Main
import com.hritwik.sassyskies.model.weather.detail.Sys
import com.hritwik.sassyskies.model.weather.detail.Weather
import com.hritwik.sassyskies.model.weather.detail.Wind

data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)