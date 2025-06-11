package com.hritwik.sassyskies.model.weather.forecast

import com.hritwik.sassyskies.model.weather.location.City

data class ForecastResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastItem>,
    val city: City
)
