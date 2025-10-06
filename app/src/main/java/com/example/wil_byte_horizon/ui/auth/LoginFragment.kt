package com.example.wil_byte_horizon.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.databinding.FragmentLoginBinding
import com.example.wil_byte_horizon.ui.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Email and password must not be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user?.isEmailVerified == true) {
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.nav_host_fragment_activity_main, HomeFragment())
                                .commit()
                        } else {
                            Toast.makeText(context, "Please verify your email before logging in.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        val error = task.exception?.localizedMessage ?: "Unknown error"
                        Toast.makeText(context, "Login failed: $error", Toast.LENGTH_LONG).show()
                    }
                }
        }

        binding.registerButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}