package com.example.wil_byte_horizon.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.wil_byte_horizon.MainActivity
import com.example.wil_byte_horizon.core.AuthResult
import com.example.wil_byte_horizon.databinding.ActivityRegisterBinding
import kotlinx.coroutines.flow.collectLatest

class RegisterActivity : ComponentActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val vm: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            vm.register(
                displayName = binding.etName.text?.toString().orEmpty(),
                email = binding.etEmail.text?.toString().orEmpty(),
                password = binding.etPassword.text?.toString().orEmpty(),
                confirm = binding.etConfirm.text?.toString().orEmpty()
            )
        }

        lifecycleScope.launchWhenStarted {
            vm.registerState.collectLatest { state ->
                when (state) {
                    is AuthResult.Loading -> binding.progress.visibility = View.VISIBLE
                    is AuthResult.Success -> { binding.progress.visibility = View.GONE; startActivity(Intent(this@RegisterActivity, MainActivity::class.java)); finish() }
                    is AuthResult.Error -> { binding.progress.visibility = View.GONE; Toast.makeText(this@RegisterActivity, state.message, Toast.LENGTH_LONG).show() }
                    null -> Unit
                }
            }
        }
    }
}
