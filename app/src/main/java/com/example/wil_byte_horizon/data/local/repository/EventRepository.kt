package com.example.wil_byte_horizon.data.repository


import com.example.wil_byte_horizon.data.local.EventDao
import com.example.wil_byte_horizon.data.local.EventEntity
import kotlinx.coroutines.flow.Flow

class EventRepository(private val eventDao: EventDao) {

    val allEventsFlow: Flow<List<EventEntity>> = eventDao.getAllEventsFlow()

    suspend fun getEventsList(): List<EventEntity> {
        return eventDao.getAllEventsList()
    }

    suspend fun addEvent(event: EventEntity) {
        eventDao.insert(event.copy(isSynced = false))
        // TODO: Implement actual network sync logic
    }

    suspend fun syncEvents() {
        val unsyncedEvents = eventDao.getAllEventsList().filter { !it.isSynced }
        if (unsyncedEvents.isNotEmpty()) {
            // TODO: Implement actual network call to upload unsyncedEvents
            // After successful sync, update their status locally
            // For example:
            // val successfullySyncedServerEvents = yourNetworkApi.syncEvents(unsyncedEvents)
            // val updatedLocalEvents = successfullySyncedServerEvents.map { serverEvent ->
            //    unsyncedEvents.find { it.localId == serverEvent.localId }?.copy(serverId = serverEvent.id, isSynced = true)
            // }.filterNotNull()
            // eventDao.insertAll(updatedLocalEvents)
            println("SyncEvents called. Found ${unsyncedEvents.size} unsynced events. (Actual sync not implemented)")

            // For now, let's simulate syncing them locally for UI testing
            val simulatedSyncedEvents = unsyncedEvents.map { it.copy(isSynced = true) }
            eventDao.insertAll(simulatedSyncedEvents)
        } else {
            println("SyncEvents called. No events to sync.")
        }
    }

    suspend fun getEventById(id: Int): EventEntity? {
        return eventDao.getEventById(id)
    }

    suspend fun updateEvent(event: EventEntity) {
        eventDao.update(event)
    }

    suspend fun deleteEvent(event: EventEntity) {
        eventDao.delete(event)
    }
}
