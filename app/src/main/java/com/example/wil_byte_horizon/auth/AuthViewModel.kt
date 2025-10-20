package com.example.wil_byte_horizon.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wil_byte_horizon.core.AuthResult
import com.example.wil_byte_horizon.core.FirebaseAuthManager
import com.example.wil_byte_horizon.core.FirestoreManager
import com.example.wil_byte_horizon.core.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<AuthResult<Unit>?>(null)
    val loginState: StateFlow<AuthResult<Unit>?> = _loginState

    private val _registerState = MutableStateFlow<AuthResult<Unit>?>(null)
    val registerState: StateFlow<AuthResult<Unit>?> = _registerState

    fun login(email: String, password: String) {
        if (!email.isValidEmail() || password.length < 6) {
            _loginState.value = AuthResult.Error("Check email and password.")
            return
        }
        _loginState.value = AuthResult.Loading
        viewModelScope.launch {
            try {
                FirebaseAuthManager.login(email.trim(), password)
                _loginState.value = AuthResult.Success(Unit)
            } catch (e: Exception) {
                _loginState.value = AuthResult.Error(e.message ?: "Login failed.", e)
            }
        }
    }

    fun register(displayName: String, email: String, password: String, confirm: String) {
        when {
            displayName.isBlank() -> { _registerState.value = AuthResult.Error("Display name required."); return }
            !email.isValidEmail() -> { _registerState.value = AuthResult.Error("Invalid email."); return }
            password.length < 6 -> { _registerState.value = AuthResult.Error("Password must be 6+ chars."); return }
            password != confirm -> { _registerState.value = AuthResult.Error("Passwords do not match."); return }
        }
        _registerState.value = AuthResult.Loading
        viewModelScope.launch {
            try {
                val user = FirebaseAuthManager.register(email.trim(), password)
                val profile = UserProfile(uid = user.uid, email = user.email.orEmpty(), displayName = displayName.trim())
                FirestoreManager.createUserProfile(profile)
                _registerState.value = AuthResult.Success(Unit)
            } catch (e: Exception) {
                _registerState.value = AuthResult.Error(e.message ?: "Registration failed.", e)
            }
        }
    }
}

private fun String.isValidEmail(): Boolean =
    android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
