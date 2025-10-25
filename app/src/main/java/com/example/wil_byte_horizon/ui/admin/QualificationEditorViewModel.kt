package com.example.wil_byte_horizon.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wil_byte_horizon.data.admin.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class QualEditorState(val loading: Boolean = false, val error: String? = null, val saved: Boolean = false)

class QualificationEditorViewModel(
    private val repo: AdminRepository = AdminRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(QualEditorState())
    val state: StateFlow<QualEditorState> = _state

    fun upsert(id: String, title: String, description: String, category: String, isOpen: Boolean) {
        if (id.isBlank()) { _state.value = QualEditorState(error = "ID is required (e.g. ag201)."); return }
        if (title.isBlank()) { _state.value = QualEditorState(error = "Title is required."); return }

        _state.value = QualEditorState(loading = true)
        viewModelScope.launch {
            try {
                repo.upsertQualification(id, title, description, category, isOpen)
                _state.value = QualEditorState(saved = true)
            } catch (e: Exception) {
                _state.value = QualEditorState(error = e.message ?: "Failed to save.")
            }
        }
    }
}
