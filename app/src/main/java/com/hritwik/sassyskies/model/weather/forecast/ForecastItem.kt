package com.hritwik.sassyskies.model.weather.forecast

import com.google.gson.annotations.SerializedName
import com.hritwik.sassyskies.model.weather.detail.Clouds
import com.hritwik.sassyskies.model.weather.location.ForecastSys
import com.hritwik.sassyskies.model.weather.detail.Main
import com.hritwik.sassyskies.model.weather.precipitation.Rain
import com.hritwik.sassyskies.model.weather.precipitation.Snow
import com.hritwik.sassyskies.model.weather.detail.Weather
import com.hritwik.sassyskies.model.weather.detail.Wind

data class ForecastItem(
    val dt: Long,
    @SerializedName("dt_txt")
    val dtTxt: String,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double, // Probability of precipitation
    val rain: Rain? = null,
    val snow: Snow? = null,
    val sys: ForecastSys
)