package com.example.wil_byte_horizon.ui.event

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.size.Scale
import com.example.wil_byte_horizon.databinding.ItemEventBinding
import com.google.firebase.storage.FirebaseStorage

class EventsAdapter(
    private val isAdmin: Boolean,
    private val onOpenMap: (UiEvent) -> Unit,
    private val onEdit: (UiEvent) -> Unit,
    private val onDelete: (UiEvent) -> Unit
) : ListAdapter<UiEvent, EventsAdapter.VH>(DIFF) {

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<UiEvent>() {
            override fun areItemsTheSame(oldItem: UiEvent, newItem: UiEvent) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: UiEvent, newItem: UiEvent) = oldItem == newItem
        }
    }

    inner class VH(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            textTitle.text = item.title
            textDate.text = item.dateRange
            textLocation.text = item.locationName
            textDescription.text = item.description

            bindPoster(imagePoster, item.imageUrl)

            btnOpenMap.setOnClickListener { onOpenMap(item) }

            // --- Admin overflow actions ---
            btnOverflow.visibility = if (isAdmin) View.VISIBLE else View.GONE
            btnOverflow.setOnClickListener { v ->
                PopupMenu(v.context, v).apply {
                    menu.add("Edit")
                    menu.add("Delete")
                    setOnMenuItemClickListener { mi ->
                        when (mi.title?.toString()) {
                            "Edit" -> onEdit(item)
                            "Delete" -> onDelete(item)
                        }
                        true
                    }
                }.show()
            }
        }
    }

    private fun bindPoster(view: ImageView, url: String?) {
        if (url.isNullOrBlank()) {
            view.setImageDrawable(null)
            return
        }

        fun loadCoil(resolved: String) {
            view.load(resolved) {
                crossfade(true)
                scale(Scale.FILL)
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
                networkCachePolicy(CachePolicy.ENABLED)
            }
        }

        if (url.startsWith("gs://")) {
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(url)
            ref.downloadUrl
                .addOnSuccessListener { loadCoil(it.toString()) }
                .addOnFailureListener { e ->
                    Log.e("EventsAdapter", "Failed to resolve downloadUrl for $url", e)
                    view.setImageDrawable(null)
                }
        } else {
            loadCoil(url)
        }
    }
}
