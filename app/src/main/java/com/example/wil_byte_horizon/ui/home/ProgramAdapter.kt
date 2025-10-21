package com.example.wil_byte_horizon.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wil_byte_horizon.R

class ProgramAdapter(
    private val onClick: (ProgramUi) -> Unit
) : ListAdapter<ProgramUi, ProgramAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<ProgramUi>() {
        override fun areItemsTheSame(oldItem: ProgramUi, newItem: ProgramUi) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ProgramUi, newItem: ProgramUi) = oldItem == newItem
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.txtProgramTitle)
        private val desc: TextView = view.findViewById(R.id.txtProgramDesc)

        fun bind(item: ProgramUi) {
            title.text = item.title
            desc.text = item.description
            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_program_chip, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
}
