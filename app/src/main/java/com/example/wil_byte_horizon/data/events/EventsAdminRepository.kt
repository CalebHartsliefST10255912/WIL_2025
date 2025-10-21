package com.example.wil_byte_horizon.data.events

import com.example.wil_byte_horizon.core.StorageManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await
import java.text.Normalizer
import java.util.Locale

class EventsAdminRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val coll get() = db.collection("events")

    /**
     * Creates an event document.
     * - If localImageUri is present, uploads to Storage then saves download URL.
     * - Else if imageUrl provided, uses it directly.
     * - Otherwise saves without image.
     * Returns the created document ID.
     */
    suspend fun createEvent(input: EventInput): String {
        val id = makeDocId(input.startAt, input.title)
        val imageUrl = when {
            input.localImageUri != null -> {
                val storagePath = "events/${storageDateFolder(input.startAt)}/${slug(input.title)}/poster.jpg"
                StorageManager.uploadAndGetUrl(storagePath, input.localImageUri)
            }
            !input.imageUrl.isNullOrBlank() -> input.imageUrl
            else -> ""
        }

        val data = hashMapOf(
            "title" to input.title,
            "description" to input.description,
            "startAt" to input.startAt,
            "endAt" to (input.endAt ?: input.startAt),
            "locationName" to input.locationName,
            "location" to GeoPoint(input.latitude, input.longitude), // ⬅️ single GeoPoint field
            "imageUrl" to imageUrl,
            "updatedAt" to Timestamp.now()
        )

        coll.document(id).set(data).await()
        return id
    }

    private fun makeDocId(startAt: Timestamp, title: String): String {
        // 20251029-1400-title-slug
        val cal = java.util.Calendar.getInstance().apply { time = startAt.toDate() }
        val y = cal.get(java.util.Calendar.YEAR)
        val m = (cal.get(java.util.Calendar.MONTH) + 1).toString().padStart(2, '0')
        val d = cal.get(java.util.Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        val h = cal.get(java.util.Calendar.HOUR_OF_DAY).toString().padStart(2, '0')
        val min = cal.get(java.util.Calendar.MINUTE).toString().padStart(2, '0')
        return "${y}${m}${d}-${h}${min}-${slug(title)}"
    }

    private fun storageDateFolder(ts: Timestamp): String {
        val cal = java.util.Calendar.getInstance().apply { time = ts.toDate() }
        val y = cal.get(java.util.Calendar.YEAR)
        val m = (cal.get(java.util.Calendar.MONTH) + 1).toString().padStart(2, '0')
        val d = cal.get(java.util.Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        return "$y/$m/$d"
    }

    private fun slug(input: String): String {
        val nowhitespace = input.trim().lowercase(Locale.US).replace("\\s+".toRegex(), "-")
        val normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD)
        val slug = normalized.replace("[^a-z0-9-]".toRegex(), "")
        return slug.take(60).trim('-') // keep slugs readable
    }
}
