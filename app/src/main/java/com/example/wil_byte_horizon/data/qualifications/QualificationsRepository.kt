package com.example.wil_byte_horizon.data.qualifications

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class QualificationsRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val col get() = db.collection("qualifications")

    /** Live stream of all open qualifications (ordered by updatedAt desc). */
    fun streamOpen(): Flow<List<Qualification>> = callbackFlow {
        val registration = col
            .whereEqualTo("isOpen", true)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val items = snap?.documents?.map { doc ->
                    doc.toObject(Qualification::class.java)?.copy(id = doc.id)
                }?.filterNotNull().orEmpty()
                trySend(items)
            }
        awaitClose { registration.remove() }
    }

    /** Optional: fetch single qualification by id. */
    suspend fun getById(id: String): Qualification? {
        val doc = col.document(id).get().await()
        return doc.toObject(Qualification::class.java)?.copy(id = doc.id)
    }
}
