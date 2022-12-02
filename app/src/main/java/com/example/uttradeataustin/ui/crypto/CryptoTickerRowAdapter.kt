package com.example.uttradeataustin.ui.crypto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
//import com.example.uttradeataustin.DataPoint
import com.example.uttradeataustin.databinding.CryptoRowTickerBinding
import com.example.uttradeataustin.api.crypto.CryptoPriceData
import com.example.uttradeataustin.MainViewModel

class CryptoTickerRowAdapter(private val viewModel: MainViewModel)
    : ListAdapter<CryptoPriceData, CryptoTickerRowAdapter.VH>(CryptoDiff()) {
    class CryptoDiff : DiffUtil.ItemCallback<CryptoPriceData>() {
        override fun areItemsTheSame(oldItem: CryptoPriceData, newItem: CryptoPriceData): Boolean {
            return oldItem.symbol == newItem.symbol
        }
        override fun areContentsTheSame(oldItem: CryptoPriceData, newItem: CryptoPriceData): Boolean {
            return oldItem.cryptoBidPrice == newItem.cryptoBidPrice && oldItem.cryptoAskPrice == newItem.cryptoAskPrice && oldItem.cryptoPrice == newItem.cryptoPrice && oldItem.latest_trading_day == newItem.latest_trading_day
        }
    }
    private var prices = mutableListOf<CryptoPriceData>()

    private fun getPos(holder: RecyclerView.ViewHolder) : Int {
        val pos = holder.bindingAdapterPosition
        // notifyDataSetChanged was called, so position is not known
        if( pos == RecyclerView.NO_POSITION) {
            return holder.absoluteAdapterPosition
        }
        return pos
    }
    inner class VH(val rowBinding: CryptoRowTickerBinding)
        :RecyclerView.ViewHolder(rowBinding.root) {
            init{
                prices = viewModel.observeCryptoTicker().value!!.toMutableList()
            }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = CryptoRowTickerBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = prices[position]
        val rowBinding = holder.rowBinding

        if (item != null){
            rowBinding.cryptoRowTickerName.text = item.symbolName
            rowBinding.cryptoRowTicker.text = item.symbol
            rowBinding.cryptoRowPrice.text = item.cryptoPrice.toString()
        }

        rowBinding.removeCryptoIv.setOnClickListener {
            val symToDelete = prices[position].symbol
            prices.removeAt(position)
            submitList(prices)
            val vmPos = getPos(holder)
            viewModel.removeCryptoTickerAt(vmPos, symToDelete)
            notifyDataSetChanged()
        }


    }

    override fun submitList(list: MutableList<CryptoPriceData>?) {
        if (list != null) {
            prices = list
        }
    }
    override fun getItemCount(): Int {
        return prices.size
    }
}