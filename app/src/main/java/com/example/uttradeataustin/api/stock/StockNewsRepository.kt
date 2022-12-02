package com.example.uttradeataustin.api.stock

class StockNewsRepository(private val api: StockNewsApi) {
    suspend fun getNews(symbol: String): List<StockNewsData>{
        val stockNewsRes = api.getNews(symbol)
        return stockNewsRes.data

    }
}