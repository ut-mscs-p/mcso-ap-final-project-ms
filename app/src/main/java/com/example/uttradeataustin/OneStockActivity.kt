package com.example.uttradeataustin

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.commit
import com.example.uttradeataustin.api.stock.StockCandles
import com.example.uttradeataustin.databinding.OneStockViewBinding
import com.example.uttradeataustin.ui.stock.news.NewsFragment
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.ArrayList

class OneStockActivity:AppCompatActivity() {
    companion object {
        const val symbolKey = "symbol"
    }

    private val oneStockFragTag = "oneStockFragTag"
    private val viewModel: MainViewModel by viewModels()
    private var timeSeriesInterval = "1d"

    private fun doFinish() {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val oneStockActivityBinding = OneStockViewBinding.inflate(layoutInflater)
        setContentView(oneStockActivityBinding.root)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title =  intent.getStringExtra(symbolKey)
        supportFragmentManager.commit {
            add(R.id.one_stock_news_frame, NewsFragment.newInstance(), oneStockFragTag)
        }

        intent.getStringExtra(symbolKey)?.let { viewModel.oneStockTicker(it, "1d") }
        viewModel.netRefreshNews()

        viewModel.observeOneStock().observe(this){
            var gv = oneStockActivityBinding.graphView
            // populate graph
            val dataPoints = generateDataPoints(it[0].stockCandles)
            val xLabels = generateXLabel(it[0].stockCandles)

            // add data points to our graph view
            val lineDataSet = LineDataSet(dataPoints, it[0].symbol)
            lineDataSet.setDrawCircles(false)
            lineDataSet.lineWidth = 5f
            val lineData = LineData(lineDataSet)
            gv.legend.isEnabled = false
            gv.description.isEnabled = false
            gv.axisRight.isEnabled = false
            gv.axisLeft.textSize = 6f
            gv.xAxis.textSize = 6f
            gv.xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
            gv.data = lineData
            gv.invalidate()
        }

        // Configure buttons
        oneStockActivityBinding.timeseries1dButton.background = Color.WHITE.toDrawable()
        oneStockActivityBinding.timeseries7dButton.background =  Color.GRAY.toDrawable()
        oneStockActivityBinding.timeseries30dButton.background =  Color.GRAY.toDrawable()

        // 1d button
        oneStockActivityBinding.timeseries1dButton.setOnClickListener {
            if (it.background != Color.WHITE.toDrawable()){
                it.background = Color.WHITE.toDrawable()
                oneStockActivityBinding.timeseries30dButton.background =  Color.GRAY.toDrawable()
                oneStockActivityBinding.timeseries7dButton.background =  Color.GRAY.toDrawable()

                intent.getStringExtra(symbolKey)?.let { it1 -> viewModel.oneStockTicker(it1, "1d") }

            }
        }
        // 7d button
        oneStockActivityBinding.timeseries7dButton.setOnClickListener {
            if (it.background != Color.WHITE.toDrawable()) {
                timeSeriesInterval = "7d"
                it.background = Color.WHITE.toDrawable()
                oneStockActivityBinding.timeseries1dButton.background = Color.GRAY.toDrawable()
                oneStockActivityBinding.timeseries30dButton.background = Color.GRAY.toDrawable()
                intent.getStringExtra(symbolKey)?.let { it1 -> viewModel.oneStockTicker(it1, "7d") }
            }
        }
        // 30d button
        oneStockActivityBinding.timeseries30dButton.setOnClickListener {
            if (it.background != Color.WHITE.toDrawable()) {
                timeSeriesInterval = "30d"
                it.background = Color.WHITE.toDrawable()
                oneStockActivityBinding.timeseries1dButton.background = Color.GRAY.toDrawable()
                oneStockActivityBinding.timeseries7dButton.background = Color.GRAY.toDrawable()
                intent.getStringExtra(symbolKey)?.let { it1 -> viewModel.oneStockTicker(it1, "30d") }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        val item = menu.findItem(R.id.swtichViewButton)
        item.title = ""
        item.isEnabled = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        val id = item.itemId

        return when (item.itemId) {
//            R.id.swtichViewButton -> { doFinish(); switchViewButton(item); true}
////            R.id.MyListButton -> { gotoMyList(item); true }
            R.id.logOutButton -> {
                FirebaseAuth.getInstance().signOut()
                doFinish();
                startActivity(Intent(this, MainActivity::class.java))
                true}
            android.R.id.home -> {  doFinish(); true }
            // Handle in fragment
            else -> super.onOptionsItemSelected(item)
        }

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
                var simpleDateTime: SimpleDateFormat

                when (timeSeriesInterval) {
                    "1d" -> {
                        simpleDateTime = SimpleDateFormat("hh:mm")
                    }
                    "7d" -> {
                        simpleDateTime = SimpleDateFormat("M/dd hh:mm")
                    }
                    else -> {
                        simpleDateTime = SimpleDateFormat("yyyy/M/dd")
                    }
                }
                val timeLabel = simpleDateTime.format(it * 1000L)
                labels.add(timeLabel)
            }
        }
        Log.d("Time", labels.toString())
        return labels
    }
}