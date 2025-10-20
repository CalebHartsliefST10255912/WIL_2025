package com.example.wil_byte_horizon.data

import com.google.firebase.Timestamp

data class Qualification(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val isOpen: Boolean = true,
    val updatedAt: Timestamp? = null
)
