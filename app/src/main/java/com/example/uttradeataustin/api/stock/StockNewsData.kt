package com.example.uttradeataustin.api.stock

import com.google.gson.annotations.SerializedName

data class NewsSentimentResponse(
    @SerializedName("meta")
    val meta: StockNewsMeta,
    @SerializedName("data")
    val data: List<StockNewsData>,
)
data class StockNewsMeta(
    @SerializedName("found")
    val found: Int,
    @SerializedName("returned")
    val returned: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("page")
    val page: Int
)


data class StockNewsData (
    @SerializedName("uuid")
    val uuid: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("keywords")
    val keywords: String,
    @SerializedName("snippet")
    val snippet: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("image_url")
    val image_url: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("published_at")
    val published_at: String,
    @SerializedName("source")
    val source: String,
    @SerializedName("relevance_score")
    val relevance_score: Float,
    @SerializedName("entities")
    val entities: List<StockNewsEntities>
){

    override fun equals(other: Any?) : Boolean =
        if (other is StockNewsData) {
            url == other.url
        } else {
            false
        }
}

data class StockNewsEntities (
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("exchange")
    val exchange: String,
    @SerializedName("exchange_long")
    val exchange_long: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("industry")
    val industry: String,
    @SerializedName("match_score")
    val match_score: Float,
    @SerializedName("sentiment_score")
    val sentiment_score: Float,
    @SerializedName("highlights")
    val highlists: List<StockNewsHighlights>
)

data class StockNewsHighlights(
    @SerializedName("highlight")
    val highlight: String,
    @SerializedName("sentiment")
    val sentiment: Float,
    @SerializedName("highlighted_in")
    val highlighted_in: String
    )