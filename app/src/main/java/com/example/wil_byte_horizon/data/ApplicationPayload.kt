// data/ApplicationPayload.kt
package com.example.wil_byte_horizon.data

data class ApplicationPayload(
    val qualificationId: String,
    val qualificationTitle: String? = null,
    val fullName: String,
    val idNumber: String,
    val dateOfBirth: String? = null,
    val email: String,
    val phone: String? = null,
    val address1: String? = null,
    val address2: String? = null,
    val city: String? = null,
    val province: String? = null,
    val postalCode: String? = null,
    val highestEducation: String? = null,
    val employmentStatus: String? = null,
    val motivation: String? = null,
    val extra: Map<String, String> = emptyMap()
)

// helper to pass to callable
fun ApplicationPayload.toMap(): Map<String, Any?> = mapOf(
    "qualificationId" to qualificationId,
    "qualificationTitle" to qualificationTitle,
    "fullName" to fullName,
    "idNumber" to idNumber,
    "dateOfBirth" to dateOfBirth,
    "email" to email,
    "phone" to phone,
    "address1" to address1,
    "address2" to address2,
    "city" to city,
    "province" to province,
    "postalCode" to postalCode,
    "highestEducation" to highestEducation,
    "employmentStatus" to employmentStatus,
    "motivation" to motivation,
    "extra" to extra
)
