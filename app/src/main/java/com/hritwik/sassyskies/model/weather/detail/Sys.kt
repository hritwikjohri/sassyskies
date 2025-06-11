package com.hritwik.sassyskies.model.weather.detail

data class Sys(
    val type: Int? = null,
    val id: Int? = null,
    val country: String,
    val sunrise: Long,
    val sunset: Long
)
