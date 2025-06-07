package com.hritwik.sassyskies.model

data class UsageStats(
    val totalRequests: Int = 0,
    val todayRequests: Int = 0,
    val lastRequest: Long = 0L,
    val weatherApiLimit: Int = 1000,
    val geminiApiLimit: Int = 100
)
