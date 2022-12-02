package com.example.uttradeataustin.api.stock

import com.example.uttradeataustin.api.StockPriceApi

class StockPriceRepository(private val api: StockPriceApi) {
    suspend fun getPrice(symbol: String, interval: String = "1d" ): StockData {
        val token = "cdp046iad3i3u5goneigcdp046iad3i3u5gonej0"
        val stockQuote = api.getQuote(symbol, token)
        var stockCandles: StockCandles

        when (interval) {
            "7d" -> {
                stockCandles = api.getCandles(symbol, "60", stockQuote.unix_timeStamp-3600*24*7, stockQuote.unix_timeStamp, token)
            }
            "30d" -> {
                stockCandles = api.getCandles(symbol, "D", stockQuote.unix_timeStamp-3600*24*30, stockQuote.unix_timeStamp, token)
            }
            else -> {
                stockCandles = api.getCandles(symbol, "15", stockQuote.unix_timeStamp-3600*8, stockQuote.unix_timeStamp, token)
            }
        }

        return StockData(symbol, stockQuote, stockCandles)
    }
}