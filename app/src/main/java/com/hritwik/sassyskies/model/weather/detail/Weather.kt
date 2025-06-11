package com.hritwik.sassyskies.model.weather.detail

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)