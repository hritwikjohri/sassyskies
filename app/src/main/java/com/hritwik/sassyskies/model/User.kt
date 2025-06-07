package com.hritwik.sassyskies.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val weatherApiKey: String = "",
    val geminiApiKey: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isVerified: Boolean = false,
    val usageCount: Int = 0,
    val lastUsage: Long = 0L
)
