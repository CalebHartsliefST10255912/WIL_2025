package com.example.wil_byte_horizon.ui.enrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wil_byte_horizon.core.FirebaseAuthManager
import com.example.wil_byte_horizon.data.Qualification
import com.example.wil_byte_horizon.data.QualificationsRepository
import kotlinx.coroutines.flow.*

class EnrolViewModel(
    private val repo: QualificationsRepository = QualificationsRepository()
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")

    private val allOpen: StateFlow<List<Qualification>> =
        repo.streamOpen()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /** UI list filtered by search text (client-side). */
    val filtered: StateFlow<List<Qualification>> =
        combine(allOpen, searchQuery) { list, q ->
            val s = q.trim().lowercase()
            if (s.isEmpty()) list
            else list.filter {
                it.title.lowercase().contains(s) ||
                        it.description.lowercase().contains(s) ||
                        it.category.lowercase().contains(s)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateSearch(text: String) { searchQuery.value = text }

    /** Returns true if user is logged in; false if we should prompt login/register. */
    fun canEnrol(): Boolean = FirebaseAuthManager.currentUser() != null
}
