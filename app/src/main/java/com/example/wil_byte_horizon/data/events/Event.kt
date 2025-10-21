package com.example.wil_byte_horizon.data.events

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val startAt: Timestamp? = null,
    val endAt: Timestamp? = null,
    val locationName: String = "",
    val location: GeoPoint? = null,
    val imageUrl: String = "",
    val updatedAt: Timestamp? = null
)
