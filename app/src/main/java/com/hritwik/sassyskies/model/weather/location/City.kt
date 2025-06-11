package com.hritwik.sassyskies.model.weather.location

import com.hritwik.sassyskies.model.weather.detail.Coord

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int? = null,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)