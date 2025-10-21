package com.example.wil_byte_horizon.core

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

object StorageManager {
    private val storage by lazy { FirebaseStorage.getInstance() }

    /**
     * Uploads a file to the given path and returns its download URL.
     * Path example: events/2025/10/29/community-soup-kitchen/poster.jpg
     */
    suspend fun uploadAndGetUrl(path: String, fileUri: Uri): String {
        val ref = storage.reference.child(path)
        ref.putFile(fileUri).await()
        return ref.downloadUrl.await().toString()
    }
}
