package com.example.wil_byte_horizon.data.enrol

import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import com.example.wil_byte_horizon.data.ApplicationPayload

class EnrolFormRepository(
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance("europe-west1")
) {
    suspend fun submitApplication(body: ApplicationPayload): Result<String> {
        return try {
            val data = hashMapOf(
                "qualificationId" to body.qualificationId,
                "qualificationTitle" to body.qualificationTitle,
                "fullName" to body.fullName,
                "idNumber" to body.idNumber,
                "dateOfBirth" to body.dateOfBirth,
                "email" to body.email,
                "phone" to body.phone,
                "address1" to body.address1,
                "address2" to body.address2,
                "city" to body.city,
                "province" to body.province,
                "postalCode" to body.postalCode,
                "highestEducation" to body.highestEducation,
                "employmentStatus" to body.employmentStatus,
                "motivation" to body.motivation,
                "extra" to body.extra
            )

            val result = functions
                .getHttpsCallable("submitApplication")
                .call(data)
                .await()

            @Suppress("UNCHECKED_CAST")
            val map = result.data as? Map<String, Any?> ?: emptyMap()
            val ok = map["ok"] as? Boolean ?: false
            val id = map["id"] as? String ?: ""

            if (ok && id.isNotBlank()) Result.success(id)
            else Result.failure(IllegalStateException("Submission failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
