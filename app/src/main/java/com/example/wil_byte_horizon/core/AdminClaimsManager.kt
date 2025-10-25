package com.example.wil_byte_horizon.core

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Emits admin status (true/false) and auto-updates when user signs in/out
 * or when you force a token refresh (after promoting someone to admin).
 */
object AdminClaimsManager {
    fun isAdminFlow(): Flow<Boolean> = callbackFlow {
        val auth = FirebaseAuth.getInstance()

        suspend fun emitCurrent() {
            val user = auth.currentUser
            if (user == null) {
                trySend(false); return
            }
            // Get latest token; if you just changed claims server-side, call with forceRefresh=true
            val token = try { user.getIdToken(false).await() } catch (_: Exception) { null }
            val isAdmin = token?.claims?.get("admin") == true
            trySend(isAdmin)
        }

        val listener = FirebaseAuth.AuthStateListener {
            // user switched -> recalc
            // fire-and-forget; we don't block the callback thread
            kotlinx.coroutines.GlobalScope.launch { emitCurrent() }
        }
        auth.addAuthStateListener(listener)

        // initial
        kotlinx.coroutines.GlobalScope.launch { emitCurrent() }

        awaitClose { auth.removeAuthStateListener(listener) }
    }

    /**
     * Call this after you’ve changed claims on the server (Cloud Function).
     */
    suspend fun forceRefreshAndCheck(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser ?: return false
        val token = user.getIdToken(true).await() // force refresh
        return token.claims["admin"] == true
    }
}
