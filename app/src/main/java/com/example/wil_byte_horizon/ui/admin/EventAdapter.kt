package com.example.wil_byte_horizon.ui.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.data.local.EventEntity

class EventAdapter : ListAdapter<EventEntity, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textViewEventTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.textViewEventDescription)
        private val dateTextView: TextView = itemView.findViewById(R.id.textViewEventDate)
        private val locationTextView: TextView = itemView.findViewById(R.id.textViewEventLocation)
        private val syncStatusTextView: TextView = itemView.findViewById(R.id.textViewEventSyncStatus)

        fun bind(event: EventEntity) {
            titleTextView.text = event.eventTitle
            descriptionTextView.text = event.eventDescription
            dateTextView.text = itemView.context.getString(R.string.event_date_prefix, event.eventDate) // Using string resource
            locationTextView.text = itemView.context.getString(R.string.event_location_prefix, event.eventLocation) // Using string resource
            syncStatusTextView.text = itemView.context.getString(if (event.isSynced) R.string.status_synced else R.string.status_not_synced)
            syncStatusTextView.setTextColor(
                itemView.context.getColor(if (event.isSynced) R.color.sync_success else R.color.sync_pending)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentEvent = getItem(position)
        holder.bind(currentEvent)
    }

    class EventDiffCallback : DiffUtil.ItemCallback<EventEntity>() {
        override fun areItemsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: EventEntity, newItem: EventEntity): Boolean {
            return oldItem == newItem
        }
    }
}
