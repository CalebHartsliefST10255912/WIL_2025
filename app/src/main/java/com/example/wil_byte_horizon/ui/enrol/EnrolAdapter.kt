package com.example.wil_byte_horizon.ui.enrol

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.data.qualifications.Qualification

class EnrolAdapter(
    private val isAdmin: Boolean,
    private val onEnrolClick: (Qualification) -> Unit,
    private val onEdit: (Qualification) -> Unit,
    private val onDelete: (Qualification) -> Unit,
    private val onOpenDetails: (Qualification) -> Unit      // ✅ new
) : ListAdapter<Qualification, EnrolAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Qualification>() {
        override fun areItemsTheSame(old: Qualification, new: Qualification) = old.id == new.id
        override fun areContentsTheSame(old: Qualification, new: Qualification) = old == new
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        private val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        private val btnEnrol: Button = view.findViewById(R.id.btnEnrol)
        private val btnOverflow: ImageButton? = view.findViewById(R.id.btnOverflow)

        fun bind(item: Qualification) {
            tvTitle.text = item.title
            tvDesc.text  = item.description

            // Tap the whole card to open details
            itemView.setOnClickListener { onOpenDetails(item) }

            // Enrol button as before
            btnEnrol.setOnClickListener { onEnrolClick(item) }

            // Admin overflow
            btnOverflow?.apply {
                visibility = if (isAdmin) View.VISIBLE else View.GONE
                setOnClickListener { v ->
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_qualification, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
}
