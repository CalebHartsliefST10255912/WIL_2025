package com.example.wil_byte_horizon.core

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
