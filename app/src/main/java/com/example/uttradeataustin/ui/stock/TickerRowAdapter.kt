package com.example.uttradeataustin.ui.stock

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.uttradeataustin.MainViewModel
import com.example.uttradeataustin.api.stock.StockCandles
import com.example.uttradeataustin.api.stock.StockData
import com.example.uttradeataustin.databinding.RowTickerBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class TickerRowAdapter(private val viewModel: MainViewModel)
    : ListAdapter<StockData, TickerRowAdapter.VH>(TickerDiff()) {
    class TickerDiff : DiffUtil.ItemCallback<StockData>() {
        override fun areItemsTheSame(oldItem: StockData, newItem: StockData): Boolean {
            return oldItem.symbol == newItem.symbol
        }
        override fun areContentsTheSame(oldItem: StockData, newItem: StockData): Boolean {
            return oldItem.
            stockQuote.low == newItem.stockQuote.low && oldItem.stockQuote.high == newItem.stockQuote.high && oldItem.stockQuote.currentPrice == newItem.stockQuote.currentPrice && oldItem.stockQuote.unix_timeStamp == newItem.stockQuote.unix_timeStamp
        }
    }
    private var prices = mutableListOf<StockData>()

    private fun getPos(holder: RecyclerView.ViewHolder) : Int {
        val pos = holder.bindingAdapterPosition
        // notifyDataSetChanged was called, so position is not known
        if( pos == RecyclerView.NO_POSITION) {
            return holder.absoluteAdapterPosition
        }
        return pos
    }

    inner class VH(val rowBinding: RowTickerBinding)
        :RecyclerView.ViewHolder(rowBinding.root) {
            init{
                prices = viewModel.observeStockTicker().value!!.toMutableList()

                rowBinding.root.setOnClickListener {
                    val item = prices[adapterPosition]
                    MainViewModel.doOneStock(it.context, item)
                }

            }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val rowBinding = RowTickerBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = prices[position]
        val rowBinding = holder.rowBinding

        rowBinding.rowTicker.text = item.symbol
        rowBinding.rowPrice.text = item.stockQuote.currentPrice.toString()
        if (item.stockQuote.percentChange > 0) {
            val percChangeStr =  "+" + item.stockQuote.percentChange.toString() + "%"
            val spannableString = SpannableString(percChangeStr)
            spannableString.setSpan(ForegroundColorSpan(Color.GREEN), 0, percChangeStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            rowBinding.rowPercChange.text = spannableString
        } else {
            val percChangeStr = item.stockQuote.percentChange.toString() + "%"
            val spannableString = SpannableString(percChangeStr)
            spannableString.setSpan(ForegroundColorSpan(Color.RED), 0, percChangeStr.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            rowBinding.rowPercChange.text = spannableString
        }

        //graph view
        val dataPoints = generateDataPoints(item.stockCandles)
        val xLabels = generateXLabel(item.stockCandles)


        // add data points to our graph view
        val lineDataSet = LineDataSet(dataPoints, item.symbol)
        lineDataSet.setDrawCircles(false)
        lineDataSet.lineWidth = 5f
        lineDataSet.setDrawValues(false)
        val lineData = LineData(lineDataSet)
        rowBinding.graphView.legend.isEnabled = false
        rowBinding.graphView.description.isEnabled = false
        rowBinding.graphView.axisRight.isEnabled = false
        rowBinding.graphView.axisLeft.textSize = 6f
        rowBinding.graphView.xAxis.textSize = 6f
        rowBinding.graphView.xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
        rowBinding.graphView.data = lineData
        rowBinding.graphView.invalidate()

        rowBinding.bookmarkIv.setOnClickListener {
            val symToDelete = prices[position].symbol
            prices.removeAt(position)
            submitList(prices)
            val vmPos = getPos(holder)
            viewModel.removeStockTickerAt(vmPos, symToDelete)
            notifyDataSetChanged()
        }
    }

    override fun submitList(list: MutableList<StockData>?) {
        if (list != null) {
            prices = list
        }
    }
    override fun getItemCount(): Int {
        return prices.size
    }

    private fun generateDataPoints(
        stockCandles: StockCandles
    ): List<Entry> {
        return if (stockCandles.status == "ok") {
            var entries = ArrayList<Entry>();
            var idx = 0
            stockCandles.close.forEach {
                Log.d("data", it.toString())
                entries.add(Entry(idx.toFloat(), it))
                idx += 1
            }
            entries
        } else {
            listOf(
                Entry(0f, 0f)
            )
        }
    }


    private fun generateXLabel(stockCandles: StockCandles): List<String> {
        var labels = ArrayList<String>();

        if (stockCandles.status == "ok") {
            stockCandles.unix_timeStamp.forEach {
                val simpleTime = SimpleDateFormat("hh:mm")
                val timeLabel = simpleTime.format(it * 1000L)
                labels.add(timeLabel)
            }
        }
        Log.d("Time", labels.toString())
        return labels
    }
}