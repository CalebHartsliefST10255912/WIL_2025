package com.example.wil_byte_horizon.data.auth

import kotlin.random.Random

object OtpManager {
    private var currentOtp: String? = null

    fun generateOtp(): String {
        currentOtp = (100000..999999).random().toString()
        return currentOtp!!
    }

    fun verifyOtp(input: String): Boolean = input == currentOtp
}