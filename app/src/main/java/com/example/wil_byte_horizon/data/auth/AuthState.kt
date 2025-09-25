package com.example.wil_byte_horizon.data.auth

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object EmailVerificationSent : AuthState()
    object LoginSuccess : AuthState()
    data class Error(val message: String) : AuthState()
}