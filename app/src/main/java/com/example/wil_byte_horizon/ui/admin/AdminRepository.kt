package com.example.wil_byte_horizon.data.admin

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class AdminRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {

    /**
     * Creates a new event document under /events, uploads poster if provided, and writes imageUrl.
     * Firestore rules require the caller to have custom claim admin==true.
     */
    suspend fun createEvent(
        title: String,
        description: String,
        startMillis: Long,
        endMillis: Long?,
        locationName: String,
        lat: Double?,
        lng: Double?,
        posterUri: Uri?
    ) {
        val eventId = db.collection("events").document().id

        var imageUrl: String? = null
        if (posterUri != null) {
            val path = "events/$eventId/poster-${UUID.randomUUID()}.jpg"
            val ref = storage.reference.child(path)
            ref.putFile(posterUri).await()
            imageUrl = ref.downloadUrl.await().toString()
        }

        val data = hashMapOf(
            "title" to title,
            "description" to description,
            "startAt" to Timestamp(startMillis / 1000, ((startMillis % 1000) * 1_000_000).toInt()),
            "endAt" to (endMillis?.let { Timestamp(it / 1000, ((it % 1000) * 1_000_000).toInt()) }),
            "locationName" to locationName,
            "location" to if (lat != null && lng != null) GeoPoint(lat, lng) else null,
            "imageUrl" to (imageUrl ?: ""),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        db.collection("events").document(eventId).set(data).await()
    }

    /**
     * Upsert qualification /qualifications/{id}
     */
    suspend fun upsertQualification(
        id: String,
        title: String,
        description: String,
        category: String,
        isOpen: Boolean
    ) {
        val doc = hashMapOf(
            "title" to title,
            "description" to description,
            "category" to category,
            "isOpen" to isOpen,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        db.collection("qualifications").document(id).set(doc).await()
    }

    suspend fun updateEventFull(
        eventId: String,
        title: String,
        description: String,
        startMillis: Long?,
        endMillis: Long?,
        locationName: String,
        lat: Double?,
        lng: Double?,
        // One of these may be set to change image:
        newPosterUri: Uri?,     // picked from device (upload & set imageUrl)
        newImageUrl: String?    // or paste a direct URL (skips upload)
    ) {
        var imageUrlPatch: String? = null
        if (newPosterUri != null) {
            val path = "events/$eventId/poster-${UUID.randomUUID()}.jpg"
            val ref = storage.reference.child(path)
            ref.putFile(newPosterUri).await()
            imageUrlPatch = ref.downloadUrl.await().toString()
        } else if (!newImageUrl.isNullOrBlank()) {
            imageUrlPatch = newImageUrl
        }

        val patch = mutableMapOf<String, Any?>(
            "title" to title,
            "description" to description,
            "locationName" to locationName,
            "location" to if (lat != null && lng != null) GeoPoint(lat, lng) else null,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        startMillis?.let { patch["startAt"] = Timestamp(it / 1000, ((it % 1000) * 1_000_000).toInt()) }
        endMillis?.let   { patch["endAt"]   = Timestamp(it / 1000, ((it % 1000) * 1_000_000).toInt()) }
        if (imageUrlPatch != null) patch["imageUrl"] = imageUrlPatch

        db.collection("events").document(eventId).update(patch).await()
    }

    suspend fun deleteEvent(eventId: String) {
        db.collection("events").document(eventId).delete().await()
    }

    suspend fun updateQualificationFull(
        id: String,
        title: String,
        description: String,
        category: String,
        isOpen: Boolean
    ) {
        val patch = mapOf(
            "title" to title,
            "description" to description,
            "category" to category,
            "isOpen" to isOpen,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        db.collection("qualifications").document(id).update(patch).await()
    }

    suspend fun deleteQualification(id: String) {
        db.collection("qualifications").document(id).delete().await()
    }
}
