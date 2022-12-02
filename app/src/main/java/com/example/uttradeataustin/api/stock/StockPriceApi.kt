package com.example.uttradeataustin.api

import com.example.uttradeataustin.api.stock.StockCandles
import com.example.uttradeataustin.api.stock.StockQuote
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.converter.gson.GsonConverterFactory

interface StockPriceApi {
    @GET("/api/v1/quote")
    suspend fun getQuote(@Query("symbol") sym: String,
                         @Query("token") key: String) : StockQuote
    @GET("/api/v1/stock/candle")
    suspend fun getCandles(@Query("symbol") sym: String,
                         @Query("resolution") res: String,
                         @Query("from") from: Int,
                         @Query("to") to: Int,
                         @Query("token") key: String) : StockCandles

    companion object {
        var httpurl = HttpUrl.Builder()
            .scheme("https")
            .host("finnhub.io")
            .build()
        fun create(): StockPriceApi = create(httpurl)
        private fun create(httpUrl: HttpUrl): StockPriceApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    // Enable basic HTTP logging to help with debugging.
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(StockPriceApi::class.java)
        }
    }
}