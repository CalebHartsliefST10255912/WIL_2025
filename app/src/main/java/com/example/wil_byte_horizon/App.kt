package com.example.wil_byte_horizon

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // 🔎 Verbose Firestore logs to Logcat
        FirebaseFirestore.setLoggingEnabled(true)

        // Explicit persistent cache (modern API)
        val cache = PersistentCacheSettings.newBuilder().build()
        FirebaseFirestore.getInstance().firestoreSettings =
            FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(cache)
                .build()
    }
}
