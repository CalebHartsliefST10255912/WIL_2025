package com.example.wil_byte_horizon.ui.admin

import android.app.Application // Required if you need context for initial check
import androidx.lifecycle.*
import com.example.wil_byte_horizon.data.local.EventEntity
import com.example.wil_byte_horizon.data.repository.EventRepository
// Import NetworkUtils if you were to use it directly in ViewModel
// import com.example.wil_byte_horizon.utils.NetworkUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// ViewModelFactory for manual injection (replace with Hilt if you use it)
// No changes to AdminViewModelFactory
class AdminViewModelFactory(private val repository: EventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AdminViewModel(repository) as T // Removed Application context
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AdminViewModel(private val eventRepository: EventRepository) : ViewModel() {

    val events: StateFlow<List<EventEntity>> = eventRepository.allEventsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _toastMessage = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastMessage

    // LiveData to suggest to the UI if it should be in read-only mode
    // The Fragment will set this based on its network check.
    private val _isReadOnly = MutableLiveData<Boolean>(false) // Default to not read-only
    val isReadOnly: LiveData<Boolean> = _isReadOnly

    fun updateReadOnlyState(isOffline: Boolean) {
        _isReadOnly.value = isOffline
    }


    fun addNewEvent(
        title: String,
        description: String,
        date: String,
        location: String
        // No network check here; Fragment should prevent calling this if offline & read-only
    ) {
        if (title.isBlank() || date.isBlank() || location.isBlank()) {
            _toastMessage.value = "Title, Date, and Location are required."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // For a strict read-only offline, this should ideally not be called by UI.
                // If it were called, and you wanted to queue, isSynced=false is correct.
                val newEvent = EventEntity(
                    eventTitle = title,
                    eventDescription = description,
                    eventDate = date,
                    eventLocation = location
                    // isSynced defaults to false, which is good for an offline addition if it were allowed
                )
                eventRepository.addEvent(newEvent) // This will save locally
                _toastMessage.value = "Event added locally (will sync when online)!" // Message adjusted
            } catch (e: Exception) {
                _toastMessage.value = "Failed to add event: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun syncLocalEvents() { // This function should only be callable when online
        viewModelScope.launch {
            _isLoading.value = true
            try {
                eventRepository.syncEvents() // Repository attempts to sync
                _toastMessage.value = "Sync process completed."
            } catch (e: Exception) {
                _toastMessage.value = "Failed to sync events: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }
}
