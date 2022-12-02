package com.example.uttradeataustin.api.crypto

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.converter.gson.GsonConverterFactory

interface CryptoPriceApi {
    @GET("/query")
    suspend fun getPrice(@Query("function") f: String,
                         @Query("from_currency") sym: String,
                         @Query("to_currency") sym_usd: String,
                         @Query("apikey") key: String) : CurrencyExchangeRate

    companion object {
        var httpurl = HttpUrl.Builder()
            .scheme("https")
            .host("www.alphavantage.co")
            .build()
        fun create(): CryptoPriceApi = create(httpurl)
        private fun create(httpUrl: HttpUrl): CryptoPriceApi {
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
                .create(CryptoPriceApi::class.java)
        }
    }
}