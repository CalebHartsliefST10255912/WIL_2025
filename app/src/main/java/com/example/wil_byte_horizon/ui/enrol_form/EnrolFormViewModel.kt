// app/src/main/java/com/example/wil_byte_horizon/ui/enrol_form/EnrolFormViewModel.kt
package com.example.wil_byte_horizon.ui.enrol_form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wil_byte_horizon.data.ApplicationPayload
import com.example.wil_byte_horizon.repository.ApplicationsRepository
import com.example.wil_byte_horizon.repository.FirebaseApplicationsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EnrolFormViewModel(
    private val repo: ApplicationsRepository = FirebaseApplicationsRepository()
) : ViewModel() {

    // Qualification (filled from EnrolFragment -> Activity)
    private val _qualificationId = MutableStateFlow("")
    val qualificationId: StateFlow<String> = _qualificationId

    private val _qualificationTitle = MutableStateFlow("")
    val qualificationTitle: StateFlow<String> = _qualificationTitle

    fun setQualification(id: String, title: String?) {
        _qualificationId.value = id.trim()
        _qualificationTitle.value = (title ?: "").trim()
    }

    // Step 0 – Personal
    val fullName = MutableStateFlow("")
    val idNumber = MutableStateFlow("")
    val dateOfBirth = MutableStateFlow("") // optional

    // Step 1 – Contact
    val email = MutableStateFlow("")
    val phone = MutableStateFlow("")       // optional
    val address1 = MutableStateFlow("")    // optional
    val address2 = MutableStateFlow("")    // optional
    val city = MutableStateFlow("")        // optional
    val province = MutableStateFlow("")    // optional
    val postalCode = MutableStateFlow("")  // optional

    // Step 2 – Background
    val highestEducation = MutableStateFlow("")   // optional
    val employmentStatus = MutableStateFlow("")   // optional
    val motivation = MutableStateFlow("")         // optional

    // Submit state
    private val _submitting = MutableStateFlow(false)
    val submitting: StateFlow<Boolean> = _submitting

    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError

    // Validation per step index: 0=Personal, 1=Contact, 2=Background, 3=Review
    fun validateStep(step: Int): Boolean {
        return when (step) {
            0 -> {
                fullName.value.trim().isNotBlank() &&
                        idNumber.value.trim().isNotBlank()
            }
            1 -> {
                val e = email.value.trim()
                e.isNotBlank() && '@' in e
            }
            2 -> true // background optional
            3 -> {
                fullName.value.trim().isNotBlank() &&
                        idNumber.value.trim().isNotBlank() &&
                        email.value.trim().isNotBlank() &&
                        '@' in email.value.trim() &&
                        qualificationId.value.isNotBlank()
            }
            else -> true
        }
    }

    private fun toPayload(): ApplicationPayload {
        return ApplicationPayload(
            qualificationId = qualificationId.value,
            qualificationTitle = qualificationTitle.value.ifBlank { null },

            // personal
            fullName = fullName.value.trim(),
            idNumber = idNumber.value.trim(),
            dateOfBirth = dateOfBirth.value.trim().ifBlank { null },

            // contact
            email = email.value.trim(),
            phone = phone.value.trim().ifBlank { null },
            address1 = address1.value.trim().ifBlank { null },
            address2 = address2.value.trim().ifBlank { null },
            city = city.value.trim().ifBlank { null },
            province = province.value.trim().ifBlank { null },
            postalCode = postalCode.value.trim().ifBlank { null },

            // background
            highestEducation = highestEducation.value.trim().ifBlank { null },
            employmentStatus = employmentStatus.value.trim().ifBlank { null },
            motivation = motivation.value.trim().ifBlank { null },

            // misc
            extra = emptyMap()
        )
    }

    fun submit(onResult: (ok: Boolean, message: String?) -> Unit) {
        if (_submitting.value) {
            // prevent accidental double taps
            return
        }

        if (!validateStep(3)) {
            onResult(false, "Please complete required fields and accept terms.")
            return
        }

        val payload = toPayload()

        viewModelScope.launch {
            _submitting.value = true
            _lastError.value = null
            val result = repo.submitApplication(payload)
            _submitting.value = false

            result.fold(
                onSuccess = { id ->
                    // Server has already queued the success email.
                    onResult(true, "Submitted (#$id)")
                },
                onFailure = { e ->
                    _lastError.value = e.message
                    // Server attempts to queue a failure email best-effort.
                    onResult(false, e.message ?: "Failed to submit")
                }
            )
        }
    }
}
