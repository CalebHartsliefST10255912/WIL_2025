package com.example.wil_byte_horizon.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    /**
     * Registers a user and sends a verification email.
     */
    fun register(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { verifyTask ->
                            if (verifyTask.isSuccessful) {
                                onResult(true, null)
                            } else {
                                onResult(false, verifyTask.exception?.message)
                            }
                        }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    /**
     * Logs in a user and checks if their email is verified.
     */
    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                val user = auth.currentUser
                if (task.isSuccessful && user?.isEmailVerified == true) {
                    onResult(true, null)
                } else {
                    onResult(false, "Email not verified or login failed.")
                }
            }
    }

    /**
     * Resends the email verification to the current user.
     */
    fun resendVerificationEmail(onResult: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        if (user != null && !user.isEmailVerified) {
            user.sendEmailVerification()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onResult(true, null)
                    } else {
                        onResult(false, task.exception?.message)
                    }
                }
        } else {
            onResult(false, "User is null or already verified.")
        }
    }

    /**
     * Returns the currently signed-in user.
     */
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    /**
     * Signs out the current user.
     */
    fun logout() = auth.signOut()

    /**
     * Placeholder for OTP generation (to be connected to OtpManager).
     */
    fun generateOtp(): String {
        return (100000..999999).random().toString()
    }

    /**
     * Placeholder for OTP verification (to be connected to OtpManager).
     */
    fun verifyOtp(input: String, actual: String): Boolean {
        return input == actual
    }
}