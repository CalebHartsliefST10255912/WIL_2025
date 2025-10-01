package com.example.wil_byte_horizon.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.wil_byte_horizon.data.local.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events ORDER BY eventDate DESC, id DESC")
    fun getAllEventsFlow(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events ORDER BY eventDate DESC, id DESC")
    suspend fun getAllEventsList(): List<EventEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>)

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: Int): EventEntity?

    @Update
    suspend fun update(event: EventEntity)

    @Delete
    suspend fun delete(event: EventEntity)

    @Query("DELETE FROM events")
    suspend fun deleteAllEvents()
}
