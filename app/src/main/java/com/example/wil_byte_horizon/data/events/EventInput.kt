package com.example.wil_byte_horizon.data.events

import com.google.firebase.Timestamp

data class EventInput(
    val title: String,
    val description: String,
    val startAt: Timestamp,
    val endAt: Timestamp? = null,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    // Either pass a Storage-ready imageUri or a direct imageUrl (if you already have one)
    val localImageUri: android.net.Uri? = null,
    val imageUrl: String? = null
)
