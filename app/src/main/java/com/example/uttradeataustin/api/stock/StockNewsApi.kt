package com.example.uttradeataustin.api.stock

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.converter.gson.GsonConverterFactory

interface StockNewsApi {
    @GET("/v1/news/all")
    suspend fun getNews(@Query("symbols") sym: String,
                        @Query("filter_entities") topics: String = "true",
                        @Query("language") key: String = "en",
                        @Query("api_token") api_token: String = "rTDLbsIl9GL7MXdloMsKjLoVcNHJju5skkR4TTMT") : NewsSentimentResponse

    companion object {
        var httpurl = HttpUrl.Builder()
            .scheme("https")
            .host("api.marketaux.com")
            .build()

        fun create(): StockNewsApi = create(httpurl)
        private fun create(httpUrl: HttpUrl): StockNewsApi {
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
                .create(StockNewsApi::class.java)
        }
    }
}