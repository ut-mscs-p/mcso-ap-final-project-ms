package com.example.uttradeataustin.ui.stock.news

import android.content.Intent
import android.net.Uri
import com.example.uttradeataustin.MainViewModel


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.uttradeataustin.R
import com.example.uttradeataustin.api.stock.StockNewsData
import com.example.uttradeataustin.databinding.RowNewsBinding


class NewsRowAdapter(private val viewModel: MainViewModel)
    : ListAdapter<StockNewsData, NewsRowAdapter.VH>(TickerDiff()) {
    class TickerDiff : DiffUtil.ItemCallback<StockNewsData>() {
        override fun areItemsTheSame(oldItem: StockNewsData, newItem: StockNewsData): Boolean {
            return oldItem.url == newItem.url
        }
        override fun areContentsTheSame(oldItem: StockNewsData, newItem: StockNewsData): Boolean {
            return oldItem.title == newItem.title
        }
    }
    private var news = mutableListOf<StockNewsData>()

    inner class VH(val rowBinding: RowNewsBinding)
        :RecyclerView.ViewHolder(rowBinding.root) {
        init{
            rowBinding.root.setOnClickListener {
                val item = news[bindingAdapterPosition]
                val newsUrl = item.url
                val intent =  Intent(Intent.ACTION_VIEW, Uri.parse(newsUrl))
                startActivity(it.context, intent, null)
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = RowNewsBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = news[position]
        val rowBinding = holder.rowBinding

        rowBinding.rowNewsTitle.text = item.title
        rowBinding.rowNewsSummary.text = item.description

        if (item.entities[0].sentiment_score > 0.5) {
            rowBinding.rowSentimentIv.setImageResource(R.drawable.ic_baseline_thumb_up_24)
        } else {
            rowBinding.rowSentimentIv.setImageResource(R.drawable.ic_baseline_thumb_down_24)
        }

    }

    override fun submitList(list: MutableList<StockNewsData>?) {
        if (list != null) {
            news = list
        }
    }
    override fun getItemCount(): Int {
        return news.size
    }


}