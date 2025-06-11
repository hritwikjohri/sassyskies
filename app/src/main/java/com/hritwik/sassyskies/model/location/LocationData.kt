package com.hritwik.sassyskies.model.location

import java.time.LocalDateTime

data class LocationData(
    val city: String,
    val country: String,
    val region: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneId: String,
    val localtime: LocalDateTime,
    val localtimeEpoch: Long,
    val utcOffset: Double
)