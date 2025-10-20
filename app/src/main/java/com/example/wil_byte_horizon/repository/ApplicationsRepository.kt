// app/src/main/java/com/example/wil_byte_horizon/repository/ApplicationsRepository.kt
package com.example.wil_byte_horizon.repository

import com.example.wil_byte_horizon.data.ApplicationPayload
import com.example.wil_byte_horizon.data.toMap
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await

interface ApplicationsRepository {
    /**
     * Submits an application via the callable Cloud Function.
     * @return Result with new application document id on success.
     */
    suspend fun submitApplication(payload: ApplicationPayload): Result<String>
}

class FirebaseApplicationsRepository(
    private val functions: FirebaseFunctions =
        FirebaseFunctions.getInstance("europe-west1") // must match your function region
) : ApplicationsRepository {

    override suspend fun submitApplication(payload: ApplicationPayload): Result<String> {
        return try {
            val resp = functions
                .getHttpsCallable("submitApplication")
                .call(payload.toMap())
                .await()
                .data as Map<*, *>

            val ok = (resp["ok"] as? Boolean) == true
            if (!ok) return Result.failure(IllegalStateException("Function returned ok=false"))

            val id = resp["id"] as? String ?: ""
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
