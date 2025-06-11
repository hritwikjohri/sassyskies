package com.hritwik.sassyskies.model.utils

data class ErrorStates(
    val hasLocationPermission: Boolean = true,
    val locationError: String? = null,
    val weatherError: String? = null
)
