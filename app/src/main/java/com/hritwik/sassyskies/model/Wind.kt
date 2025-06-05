package com.hritwik.sassyskies.model

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
)
