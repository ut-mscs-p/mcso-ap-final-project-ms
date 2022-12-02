package com.example.uttradeataustin.api.stock

import com.google.gson.annotations.SerializedName

data class StockQuote(
    @SerializedName("c")
    val currentPrice: Float,
    @SerializedName("d")
    val change: Float,
    @SerializedName("dp")
    val percentChange: Float,
    @SerializedName("h")
    val high: Float,
    @SerializedName("l")
    val low: Float,
    @SerializedName("o")
    val open: Float,
    @SerializedName("pc")
    val previouClose: Float,
    @SerializedName("t")
    val unix_timeStamp: Int,
 )

data class StockCandles(
    @SerializedName("c")
    val close: List<Float>,
    @SerializedName("h")
    val high: List<Float>,
    @SerializedName("l")
    val low:  List<Float>,
    @SerializedName("o")
    val open: List<Float>,
    @SerializedName("s")
    val status: String,
    @SerializedName("t")
    val unix_timeStamp: List<Int>,
    @SerializedName("v")
    val volume: List<Int>
)

data class StockData(
    val symbol: String, val stockQuote: StockQuote, val stockCandles: StockCandles
) {
    override fun equals(other: Any?) : Boolean =
        if (other is StockData) {
            symbol == other.symbol
        } else {
            false
        }
}