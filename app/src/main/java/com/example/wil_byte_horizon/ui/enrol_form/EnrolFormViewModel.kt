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
        _qualificationId.value = id
        _qualificationTitle.value = title ?: ""
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
                fullName.value.isNotBlank() &&
                        idNumber.value.isNotBlank()
            }
            1 -> {
                val e = email.value.trim()
                e.isNotBlank() && '@' in e
            }
            2 -> {
                // background is optional in this baseline; always valid
                true
            }
            3 -> {
                // review: ensure minimum required overall + terms
                fullName.value.isNotBlank() &&
                        idNumber.value.isNotBlank() &&
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
            fullName = fullName.value,
            idNumber = idNumber.value,
            dateOfBirth = dateOfBirth.value.ifBlank { null },

            // contact
            email = email.value,
            phone = phone.value.ifBlank { null },
            address1 = address1.value.ifBlank { null },
            address2 = address2.value.ifBlank { null },
            city = city.value.ifBlank { null },
            province = province.value.ifBlank { null },
            postalCode = postalCode.value.ifBlank { null },

            // background
            highestEducation = highestEducation.value.ifBlank { null },
            employmentStatus = employmentStatus.value.ifBlank { null },
            motivation = motivation.value.ifBlank { null },

            // misc
            extra = emptyMap()
        )
    }

    /**
     * Call from your Activity's submit() after final validation passes.
     */
    fun submit(onResult: (ok: Boolean, message: String?) -> Unit) {
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
                    onResult(true, "Submitted (#$id)")
                },
                onFailure = { e ->
                    _lastError.value = e.message
                    onResult(false, e.message ?: "Failed to submit")
                }
            )
        }
    }
}
