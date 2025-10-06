package com.example.wil_byte_horizon.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val eventTitle: String,
    val eventDescription: String,
    val eventDate: String,
    val eventLocation: String,
    val isSynced: Boolean = false
)