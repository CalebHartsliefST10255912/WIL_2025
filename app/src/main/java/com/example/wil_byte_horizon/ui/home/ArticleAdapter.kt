package com.example.wil_byte_horizon.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.wil_byte_horizon.R

class ArticleAdapter(
    private val onClick: (ArticleUi) -> Unit
) : ListAdapter<ArticleUi, ArticleAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<ArticleUi>() {
        override fun areItemsTheSame(oldItem: ArticleUi, newItem: ArticleUi) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ArticleUi, newItem: ArticleUi) = oldItem == newItem
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        private val title: TextView = view.findViewById(R.id.txtArticleTitle)
        private val snippet: TextView = view.findViewById(R.id.txtArticleSnippet)

        fun bind(item: ArticleUi) {
            title.text = item.title
            snippet.text = item.snippet
            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_article_card, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))
}
