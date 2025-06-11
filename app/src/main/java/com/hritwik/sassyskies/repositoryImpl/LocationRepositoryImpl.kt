package com.hritwik.sassyskies.repositoryImpl

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import com.google.android.gms.location.*
import com.hritwik.sassyskies.model.location.LocationData
import com.hritwik.sassyskies.repository.LocationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.Instant
import java.time.LocalDateTime
import java.util.TimeZone
import kotlin.coroutines.resume

class LocationRepositoryImpl(private val context: Context) : LocationRepository {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Result<LocationData> {
        return try {
            val location = suspendCancellableCoroutine { continuation ->
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            continuation.resume(location)
                        } else {
                            requestFreshLocation { freshLocation ->
                                continuation.resume(freshLocation)
                            }
                        }
                    }
                    .addOnFailureListener {
                        continuation.resume(null)
                    }
            }

            if (location != null) {
                val locationData = getLocationDetails(location.latitude, location.longitude)
                Result.success(locationData)
            } else {
                Result.failure(Exception("Unable to get location"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun startLocationUpdates(intervalMs: Long): Flow<Result<LocationData>> {
        return callbackFlow {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
                .setMinUpdateIntervalMillis(intervalMs / 2)
                .setMaxUpdateDelayMillis(intervalMs * 2)
                .build()

            locationCallback = object : LocationCallback() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        val locationData = getLocationDetails(location.latitude, location.longitude)
                        trySend(Result.success(locationData))
                    } ?: trySend(Result.failure(Exception("Location is null")))
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )

            awaitClose {
                stopLocationUpdates()
            }
        }
    }

    override fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestFreshLocation(callback: (Location?) -> Unit) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMaxUpdates(1)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                callback(locationResult.lastLocation)
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLocationDetails(latitude: Double, longitude: Double): LocationData {
        return try {
            val geocoder = Geocoder(context)
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            val address = addresses?.firstOrNull()
            val city = address?.locality ?: address?.subAdminArea ?: "Unknown"
            val country = address?.countryName ?: "Unknown"
            val region = address?.adminArea ?: "Unknown"

            val timeZone = TimeZone.getDefault()
            val currentTime = LocalDateTime.now()
            val epoch = Instant.now().epochSecond
            val utcOffset = timeZone.rawOffset / (1000.0 * 60 * 60)

            LocationData(
                city = city,
                country = country,
                region = region,
                latitude = latitude,
                longitude = longitude,
                timezoneId = timeZone.id,
                localtime = currentTime,
                localtimeEpoch = epoch,
                utcOffset = utcOffset
            )
        } catch (_: Exception) {
            val timeZone = TimeZone.getDefault()
            val currentTime = LocalDateTime.now()
            val epoch = Instant.now().epochSecond
            val utcOffset = timeZone.rawOffset / (1000.0 * 60 * 60)

            LocationData(
                city = "Unknown",
                country = "Unknown",
                region = "Unknown",
                latitude = latitude,
                longitude = longitude,
                timezoneId = timeZone.id,
                localtime = currentTime,
                localtimeEpoch = epoch,
                utcOffset = utcOffset
            )
        }
    }
}