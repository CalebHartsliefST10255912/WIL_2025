package com.example.wil_byte_horizon.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wil_byte_horizon.data.auth.AuthRepository
import com.example.wil_byte_horizon.data.auth.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun register(email: String, password: String) {
        _authState.value = AuthState.Loading
        repo.register(email, password) { success, error ->
            _authState.value = if (success) {
                AuthState.EmailVerificationSent
            } else {
                AuthState.Error(error ?: "Registration failed")
            }
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        repo.login(email, password) { success, error ->
            _authState.value = if (success) {
                AuthState.LoginSuccess
            } else {
                AuthState.Error(error ?: "Login failed")
            }
        }
    }

    fun logout() {
        repo.logout()
        _authState.value = AuthState.Idle
    }
}