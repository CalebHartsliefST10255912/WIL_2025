package com.example.wil_byte_horizon.data.events

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EventsRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val coll get() = db.collection("events")

    fun eventsFlow(): Flow<EventsResult> = callbackFlow {
        val query = coll.orderBy("startAt", Query.Direction.ASCENDING)

        // IMPORTANT: include metadata changes so we get a second emission when we go online
        val registration = query.addSnapshotListener(MetadataChanges.INCLUDE) { snapshots, error ->
            if (error != null) {
                Log.e("EventsRepo", "listener error", error)
                trySend(EventsResult.Error(error))
                return@addSnapshotListener
            }
            if (snapshots != null) {
                val events = snapshots.documents.mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                }
                val fromCache = snapshots.metadata.isFromCache
                val pending = snapshots.metadata.hasPendingWrites()
                Log.d(
                    "EventsRepo",
                    "listener isFromCache=$fromCache hasPendingWrites=$pending size=${events.size}"
                )
                trySend(EventsResult.Success(events, fromCache, pending))
            }
        }

        // Force a one-time SERVER fetch to nudge online state
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val serverSnap = query.get(Source.SERVER).await()
                Log.d("EventsRepo", "server fetch OK size=${serverSnap.size()}")
            } catch (e: Exception) {
                Log.e("EventsRepo", "server fetch FAILED", e)
            }
        }

        awaitClose { registration.remove() }
    }
}

sealed class EventsResult {
    data class Success(
        val events: List<Event>,
        val isFromCache: Boolean,
        val hasPendingWrites: Boolean
    ) : EventsResult()

    data class Error(val throwable: Throwable) : EventsResult()
}
