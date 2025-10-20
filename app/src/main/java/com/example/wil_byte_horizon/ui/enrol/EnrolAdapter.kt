package com.example.wil_byte_horizon.ui.enrol

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wil_byte_horizon.R
import com.example.wil_byte_horizon.data.Qualification

class EnrolAdapter(
    private val onEnrolClick: (Qualification) -> Unit
) : ListAdapter<Qualification, EnrolAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<Qualification>() {
        override fun areItemsTheSame(old: Qualification, new: Qualification) = old.id == new.id
        override fun areContentsTheSame(old: Qualification, new: Qualification) = old == new
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        private val tvDesc: TextView = view.findViewById(R.id.tvDesc)
        private val btnEnrol: Button = view.findViewById(R.id.btnEnrol)
        fun bind(item: Qualification) {
            tvTitle.text = item.title
            tvDesc.text  = item.description
            btnEnrol.setOnClickListener { onEnrolClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_qualification, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
}
