package com.hritwik.sassyskies.model.weather.detail

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
)
