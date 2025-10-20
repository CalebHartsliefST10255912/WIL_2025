package com.example.wil_byte_horizon.core

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreManager {
    private val db by lazy { FirebaseFirestore.getInstance() }

    suspend fun createUserProfile(profile: UserProfile) {
        require(profile.uid.isNotBlank())
        db.collection("users").document(profile.uid).set(profile).await()
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        val snap = db.collection("users").document(uid).get().await()
        return snap.toObject(UserProfile::class.java)
    }
}
