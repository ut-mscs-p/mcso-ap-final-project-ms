package com.example.uttradeataustin.api.crypto

import com.google.gson.annotations.SerializedName

data class CurrencyExchangeRate(
    @SerializedName("Realtime Currency Exchange Rate")
    val cryptoPriceData: CryptoPriceData
)

data class CryptoPriceData (
    @SerializedName("1. From_Currency Code")
    val symbol: String,
    @SerializedName("2. From_Currency Name")
    val symbolName: String,
    @SerializedName("3. To_Currency Code")
    val currencyCode: String,
    @SerializedName("4. To_Currency Name")
    val currencyName: String,
    @SerializedName("5. Exchange Rate")
    val cryptoPrice: Float,
    @SerializedName("6. Last Refreshed")
    val latest_trading_day: String,
    @SerializedName("7. Time Zone")
    val utc: String,
    @SerializedName("8. Bid Price")
    val cryptoBidPrice: String,
    @SerializedName("9. Ask Price")
    val cryptoAskPrice: String,
    ){

    override fun equals(other: Any?) : Boolean =
        if (other is CryptoPriceData) {
            symbol == other.symbol
        } else {
            false
        }
}