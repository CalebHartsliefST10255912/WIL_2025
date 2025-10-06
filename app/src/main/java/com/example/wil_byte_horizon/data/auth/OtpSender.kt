package com.example.wil_byte_horizon.data.auth

object OtpSender {
    fun sendOtpToEmail(email: String, otp: String) {
        // Replace with SendGrid, Mailgun, or SMTP logic
        println("Sending OTP $otp to $email")
    }
}