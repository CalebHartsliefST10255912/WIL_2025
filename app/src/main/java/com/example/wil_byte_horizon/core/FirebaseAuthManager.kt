package com.example.wil_byte_horizon.core

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

object FirebaseAuthManager {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun currentUser(): FirebaseUser? = auth.currentUser

    suspend fun login(email: String, password: String): FirebaseUser {
        val res = auth.signInWithEmailAndPassword(email, password).await()
        return res.user ?: error("User null after login")
    }

    suspend fun register(email: String, password: String): FirebaseUser {
        val res = auth.createUserWithEmailAndPassword(email, password).await()
        return res.user ?: error("User null after register")
    }

    suspend fun loginWithGoogleIdToken(idToken: String): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val res = auth.signInWithCredential(credential).await()
        return res.user ?: error("User null after Google sign-in")
    }

    fun logout() = auth.signOut()

    suspend fun logoutAll(context: Context) {
        try { FirebaseAuth.getInstance().signOut() } finally {
            try {
                CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
            } catch (_: Exception) { /* devices without Google services */ }
        }
    }
}
