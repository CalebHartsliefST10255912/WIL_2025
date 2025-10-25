package com.example.wil_byte_horizon.ui.admin

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wil_byte_horizon.data.admin.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class EventEditorState(
    val loading: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)

class EventEditorViewModel(
    private val repo: AdminRepository = AdminRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(EventEditorState())
    val state: StateFlow<EventEditorState> = _state

    private var startMillis: Long? = null
    private var endMillis: Long? = null

    fun setStart(millis: Long) { startMillis = millis }
    fun setEnd(millis: Long) { endMillis = millis }

    fun saveEvent(
        title: String,
        description: String,
        locationName: String,
        lat: Double?,
        lng: Double?,
        poster: Uri?
    ) {
        if (title.isBlank()) { _state.value = EventEditorState(error = "Title is required."); return }
        if (startMillis == null) { _state.value = EventEditorState(error = "Start time is required."); return }

        _state.value = EventEditorState(loading = true)
        viewModelScope.launch {
            try {
                repo.createEvent(
                    title = title.trim(),
                    description = description.trim(),
                    startMillis = startMillis!!,
                    endMillis = endMillis,
                    locationName = locationName.trim(),
                    lat = lat,
                    lng = lng,
                    posterUri = poster
                )
                _state.value = EventEditorState(saved = true)
            } catch (e: Exception) {
                _state.value = EventEditorState(error = e.message ?: "Failed to save.")
            }
        }
    }
}
