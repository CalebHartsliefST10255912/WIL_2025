package com.example.wil_byte_horizon.ui.event

import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wil_byte_horizon.data.events.Event
import com.example.wil_byte_horizon.data.events.EventsRepository
import com.example.wil_byte_horizon.data.events.EventsResult
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

data class EventsUiState(
    val isLoading: Boolean = true,
    val events: List<UiEvent> = emptyList(),
    val error: String? = null,
    val isFromCache: Boolean = true,
    val hasPendingWrites: Boolean = false
) {
    val isOfflineSource: Boolean get() = isFromCache && !hasPendingWrites
}

data class UiEvent(
    val id: String,
    val title: String,
    val description: String,
    val dateRange: String,
    val locationName: String,
    val lat: Double?,
    val lng: Double?,
    val imageUrl: String
)

class EventViewModel(
    private val repo: EventsRepository = EventsRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(EventsUiState())
    val state: StateFlow<EventsUiState> = _state.asStateFlow()

    init { observeEvents() }

    private fun observeEvents() = viewModelScope.launch {
        repo.eventsFlow().collect { result ->
            when (result) {
                is EventsResult.Error -> {
                    Log.e("EventVM", "Events flow error", result.throwable)
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.throwable.localizedMessage ?: "Failed to load events"
                    )
                }
                is EventsResult.Success -> {
                    val ui = result.events.map { it.toUi() }
                    ui.forEach {
                        Log.d(
                            "EventVM",
                            "Event ${it.id} fromCache=${result.isFromCache} pending=${result.hasPendingWrites} imageUrl=${it.imageUrl}"
                        )
                    }
                    _state.value = _state.value.copy(
                        isLoading = false,
                        events = ui,
                        error = null,
                        isFromCache = result.isFromCache,
                        hasPendingWrites = result.hasPendingWrites
                    )
                }
            }
        }
    }

    fun refresh() = viewModelScope.launch {
        try {
            FirebaseFirestore.getInstance()
                .collection("events")
                .orderBy("startAt")
                .get(Source.SERVER)
                .await()
        } catch (e: Exception) {
            Log.e("EventVM", "Refresh failed", e)
            _state.value = _state.value.copy(error = e.localizedMessage ?: "Refresh failed")
        }
    }

    // ---- mapping helpers ----
    private fun Event.toUi(): UiEvent {
        val lat = this.location?.latitude
        val lng = this.location?.longitude
        return UiEvent(
            id = this.id,
            title = this.title,
            description = this.description,
            dateRange = formatRange(this.startAt, this.endAt),
            locationName = this.locationName,
            lat = lat,
            lng = lng,
            imageUrl = this.imageUrl
        )
    }

    private fun formatRange(start: Timestamp?, end: Timestamp?): String {
        if (start == null && end == null) return ""
        if (start != null && end == null) return formatTs(start)
        if (start == null && end != null) return formatTs(end)
        return "${formatTs(start!!)} – ${formatTs(end!!)}"
    }

    private fun formatTs(ts: Timestamp): String {
        val d: Date = ts.toDate()
        return DateFormat.format("EEE, dd MMM yyyy • HH:mm", d).toString()
    }
}
