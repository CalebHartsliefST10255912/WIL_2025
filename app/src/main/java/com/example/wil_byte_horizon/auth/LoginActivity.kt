package com.example.wil_byte_horizon.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.example.wil_byte_horizon.MainActivity
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.core.AuthResult
import com.example.wil_byte_horizon.core.FirebaseAuthManager
import com.example.wil_byte_horizon.core.FirestoreManager
import com.example.wil_byte_horizon.core.UserProfile
import com.example.wil_byte_horizon.databinding.ActivityLoginBinding
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val vm: AuthViewModel by viewModels()
    private val credentialManager by lazy { CredentialManager.create(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Email/password
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text?.toString().orEmpty()
            val pass = binding.etPassword.text?.toString().orEmpty()
            vm.login(email, pass)
        }
        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // Google button
        binding.btnGoogle.setOnClickListener { signInWithGoogle() }

        lifecycleScope.launchWhenStarted {
            vm.loginState.collect { state ->
                when (state) {
                    is AuthResult.Loading -> binding.progress.visibility = View.VISIBLE
                    is AuthResult.Success -> { binding.progress.visibility = View.GONE; goHome() }
                    is AuthResult.Error -> {
                        binding.progress.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_LONG).show()
                    }
                    null -> Unit
                }
            }
        }
    }

    private fun signInWithGoogle() {
        binding.progress.visibility = View.VISIBLE

        val googleIdOption = GetGoogleIdOption.Builder()
            // *** IMPORTANT: this must be your WEB (server) client ID from Google Cloud Console ***
            .setServerClientId(getString(R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val response = credentialManager.getCredential(this@LoginActivity, request)
                handleCredential(response.credential)
            } catch (e: Exception) {
                binding.progress.visibility = View.GONE
                Log.e("LoginActivity", "getCredential error", e)
                Toast.makeText(this@LoginActivity, "Google sign-in failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleCredential(credential: Credential) {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val tokenCred = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = tokenCred.idToken
            lifecycleScope.launch {
                try {
                    val user = FirebaseAuthManager.loginWithGoogleIdToken(idToken)
                    // Upsert profile if first time
                    if (FirestoreManager.getUserProfile(user.uid) == null) {
                        val profile = UserProfile(
                            uid = user.uid,
                            email = user.email.orEmpty(),
                            displayName = user.displayName.orEmpty()
                        )
                        FirestoreManager.createUserProfile(profile)
                    }
                    binding.progress.visibility = View.GONE
                    goHome()
                } catch (e: Exception) {
                    binding.progress.visibility = View.GONE
                    Log.e("LoginActivity", "Firebase auth error", e)
                    Toast.makeText(this@LoginActivity, "Auth failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            binding.progress.visibility = View.GONE
            Toast.makeText(this, "Not a Google Sign-In credential.", Toast.LENGTH_LONG).show()
        }
    }

    private fun goHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    // Optional: if you add a "sign out everywhere" here
    private fun clearCredentialState() {
        lifecycleScope.launch {
            try { credentialManager.clearCredentialState(ClearCredentialStateRequest()) }
            catch (_: Exception) { /* ignore */ }
        }
    }
}
