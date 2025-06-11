package com.hritwik.sassyskies.model.weather.precipitation

import com.google.gson.annotations.SerializedName

data class Snow(
    @SerializedName("3h")
    val threeHour: Double? = null
)