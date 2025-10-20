package com.example.wil_byte_horizon.core

sealed class AuthResult<out T> {
    data class Success<T>(val data: T? = null) : AuthResult<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : AuthResult<Nothing>()
    data object Loading : AuthResult<Nothing>()
}
