package com.example.uttradeataustin

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.uttradeataustin.model.CryptoSymbol
import com.example.uttradeataustin.model.StockSymbol
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ViewModelDBHelper {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val stockCollection = "allStockSymbols"
    private val cryptoCollection = "allCryptoSymbols"

    // fetch
    fun fetchStockSymbols(stockSymbolLiveList: MutableLiveData<List<StockSymbol>>, currentUser: FirebaseUser) {
        val sortOrder = Query.Direction.ASCENDING
        val sortField = "symbol"
        val query = db.collection(stockCollection)
            .whereEqualTo("ownerUid", currentUser.uid)
            .orderBy(sortField, sortOrder)
        limitAndGetStock(query, stockSymbolLiveList)
    }

    fun fetchCryptoSymbols(cryptoSymbolLiveList: MutableLiveData<List<CryptoSymbol>>, currentUser: FirebaseUser) {
        val sortOrder = Query.Direction.ASCENDING
        val sortField = "symbol"
        val query = db.collection(cryptoCollection)
            .whereEqualTo("ownerUid", currentUser.uid)
            .orderBy(sortField, sortOrder)
        limitAndGetCrypto(query, cryptoSymbolLiveList)
    }

    // create
    fun createStockSymbols(
        stockSymbol: StockSymbol,
        stockSymbolLiveList: MutableLiveData<List<StockSymbol>>, currentUser: FirebaseUser) {

        db.collection(stockCollection)
            .add(stockSymbol)
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "StockSymbol create \"${stockSymbol.symbol}\" id: ${stockSymbol.firestoreID}"
                )
                fetchStockSymbols(stockSymbolLiveList, currentUser)
            }
            .addOnFailureListener { e ->
                Log.d(javaClass.simpleName, "Note create FAILED \"${stockSymbol.symbol}\"")
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }
    fun createCryptoymbols(
        cryptoSymbol: CryptoSymbol,
        cryptoSymbolLiveList: MutableLiveData<List<CryptoSymbol>>, currentUser: FirebaseUser) {

        db.collection(cryptoCollection)
            .add(cryptoSymbol)
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "CryptoSymbol create \"${cryptoSymbol.symbol}\" id: ${cryptoSymbol.firestoreID}"
                )
                fetchCryptoSymbols(cryptoSymbolLiveList, currentUser)
            }
            .addOnFailureListener { e ->
                Log.d(javaClass.simpleName, "Symbol create FAILED \"${cryptoSymbol.symbol}\"")
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    // remove
    fun removeStockSymbols(
        stockSymbol: StockSymbol,
        stockSymbolLiveList: MutableLiveData<List<StockSymbol>>,
        currentUser: FirebaseUser) {

        db.collection(stockCollection)
            .document(stockSymbol.firestoreID)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "StockSymbol delete \"${stockSymbol.symbol}\" id: ${stockSymbol.firestoreID}"
                )
                fetchStockSymbols(stockSymbolLiveList, currentUser)
            }
            .addOnFailureListener { e ->
                Log.d(javaClass.simpleName, "Symbol deleting FAILED \"${stockSymbol.symbol}\"")
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    fun removeCryptoSymbols(
        cryptoSymbol: CryptoSymbol,
        cryptoSymbolLiveList: MutableLiveData<List<CryptoSymbol>>,
        currentUser: FirebaseUser) {

        db.collection(cryptoCollection)
            .document(cryptoSymbol.firestoreID)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "StockSymbol delete \"${cryptoSymbol.symbol}\" id: ${cryptoSymbol.firestoreID}"
                )
                fetchCryptoSymbols(cryptoSymbolLiveList, currentUser)
            }
            .addOnFailureListener { e ->
                Log.d(javaClass.simpleName, "Symbol deleting FAILED \"${cryptoSymbol.symbol}\"")
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    private fun limitAndGetStock(query: Query,
                            stockSymbolLiveList: MutableLiveData<List<StockSymbol>>
    ) {
        query
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "StockSymbol fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                stockSymbolLiveList.postValue(result.documents.mapNotNull {
                    it.toObject(StockSymbol::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "StockSymbol fetch FAILED ", it)
            }
    }

    private fun limitAndGetCrypto(query: Query,
                                 cryptoSymbolLiveList: MutableLiveData<List<CryptoSymbol>>
    ) {
        query
            .limit(100)
            .get()
            .addOnSuccessListener { result ->
                Log.d(javaClass.simpleName, "CryptoSymbol fetch ${result!!.documents.size}")
                // NB: This is done on a background thread
                cryptoSymbolLiveList.postValue(result.documents.mapNotNull {
                    it.toObject(CryptoSymbol::class.java)
                })
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "CryptoSymbol fetch FAILED ", it)
            }
    }


}