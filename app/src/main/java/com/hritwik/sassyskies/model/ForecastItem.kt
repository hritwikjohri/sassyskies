package com.hritwik.sassyskies.model

import com.google.gson.annotations.SerializedName

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