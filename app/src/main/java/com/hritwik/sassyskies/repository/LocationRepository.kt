package com.hritwik.sassyskies.repository

import com.hritwik.sassyskies.model.location.LocationData
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getCurrentLocation(): Result<LocationData>
    suspend fun startLocationUpdates(intervalMs: Long = 60000): Flow<Result<LocationData>>
    fun stopLocationUpdates()
}