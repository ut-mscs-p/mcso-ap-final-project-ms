package com.example.uttradeataustin.api.crypto

import com.example.uttradeataustin.api.crypto.CryptoPriceApi
import com.example.uttradeataustin.api.crypto.CurrencyExchangeRate

class CryptoPriceRepository(private val api: CryptoPriceApi) {
    suspend fun getPrice(from_currency: String, to_currency: String): CurrencyExchangeRate {
        return api.getPrice( "CURRENCY_EXCHANGE_RATE", from_currency, to_currency, "BXHF6UZ9HKBA8J0L")
    }
}
