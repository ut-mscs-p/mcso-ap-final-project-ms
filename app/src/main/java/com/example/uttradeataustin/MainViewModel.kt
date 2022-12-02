package com.example.uttradeataustin

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.*
import com.example.uttradeataustin.api.StockPriceApi
import com.example.uttradeataustin.api.crypto.CryptoPriceApi
import com.example.uttradeataustin.api.crypto.CryptoPriceData
import com.example.uttradeataustin.api.crypto.CryptoPriceRepository
import com.example.uttradeataustin.api.stock.*
import com.example.uttradeataustin.model.CryptoSymbol
import com.example.uttradeataustin.model.StockSymbol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext


class MainViewModel : ViewModel() {
    // Symbol Variables
    private var defaultStockSymbols = listOf("AAPL", "AMZN", "GOOGL", "IBM")
    private var defaultCryptoSymbols = listOf("BTC", "ETH")
    var stockSymbols = arrayListOf<String>()
    var cryptoSymbols = arrayListOf<String>()
    var favStockPrices = MutableLiveData<List<StockData>>()

    // API variables
    private val stockPriceApi = StockPriceApi.create()
    private val stockNewsApi = StockNewsApi.create()
    private val cryptoPriceApi = CryptoPriceApi.create()
    // Repository variables
    private val stockPriceRepository = StockPriceRepository(stockPriceApi)
    private val stockNewsRepository = StockNewsRepository(stockNewsApi)
    private val cryptoPriceRepository = CryptoPriceRepository(cryptoPriceApi)
    // Livedata Variables
    private var stockPrices = MutableLiveData<List<StockData>>()
    private var stockNews = MutableLiveData<List<StockNewsData>>()
    private var cryptoPrices = MutableLiveData<List<CryptoPriceData>>()

    // Other variables
//    private var allStockSymbols = stockSymbols  // default stocks plus stocks added by user
    private var cryptoSymCurrency = "USD"
//    private var allCryptoSymbols = cryptoSymbols  // default coins plus coins added by user
    private var respAlert = MutableLiveData<Boolean?>()
    var isStockInit = false
    var isCryptoInit = false

    // Firestore state
    private var firebaseAuthLiveData = FirestoreAuthLiveData()

    // Database access
    private val dbHelp = ViewModelDBHelper()
    private var stockSymbolLiveList = MutableLiveData<List<StockSymbol>>()
    private var cryptoSymbolLiveList = MutableLiveData<List<CryptoSymbol>>()

    // OneStock Data
    private lateinit var oneStockTicker: String
    private val oneStockPrice = MutableLiveData<List<StockData>>()

    // fetch Done variables
    var fetchStockDone: MutableLiveData<Boolean> = MutableLiveData(false)
    var fetchCryptoDone: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
    }

    fun netRefreshStock() {
        fetchStockDone.postValue(false)

        fetchStockLists()

        viewModelScope.launch {
            withContext(coroutineContext) {
                val content = arrayListOf<StockData>()

                coroutineScope {
                    stockSymbols.forEach { sym ->
                        launch { // this will allow us to run multiple tasks in parallel
                            val apiResponse = stockPriceRepository.getPrice(sym)
                            content.add(apiResponse)
                        }
                    }
                }  // coroutineScope block will wait here until all child tasks are completed
                stockPrices.postValue(content)
            }
        }
        fetchStockDone.postValue(true)
    }
    fun netStock(sym: String) {
        val currentUser = firebaseAuthLiveData.getCurrentUser()!!

        viewModelScope.launch(
            context = viewModelScope.coroutineContext +
                    Dispatchers.IO) {
            if (sym.isNotEmpty() && !stockSymbols.contains(sym)) {
                val apiResponse = stockPriceRepository.getPrice(sym)

                if (apiResponse.stockCandles.high != null) {
                    val newStockPrices = stockPrices.value!!.toMutableList()
                    newStockPrices.add(apiResponse)
                    stockPrices.postValue(newStockPrices)
//                    allStockSymbols += sym // added coin to fav list
                    stockSymbols.add(sym) // we can reuse default variable here
                    Log.d("SymbolList", stockSymbols.toString())

                    val stockSymbol = StockSymbol (
                        symbol = sym,
                        ownerName = currentUser.displayName ?: "Anonymous user",
                        ownerUid = currentUser.uid,
                    )

                    dbHelp.createStockSymbols(stockSymbol, stockSymbolLiveList, currentUser)

                    respAlert.postValue(false)
                } else {
                    respAlert.postValue(true)
                }
            }
        }
    }

    fun netOneStock(sym: String, interval: String) {
        viewModelScope.launch(
            context = viewModelScope.coroutineContext +
                    Dispatchers.IO) {
            oneStockPrice.postValue(listOf( stockPriceRepository.getPrice(sym, interval)))
        }
    }
    fun netCrypto(sym: String) {
        val currentUser = firebaseAuthLiveData.getCurrentUser()!!

        viewModelScope.launch(
            context = viewModelScope.coroutineContext +
                    Dispatchers.IO) {

            if (sym.isNotEmpty() && !cryptoSymbols.contains(sym.uppercase())) {
                val apiResponse =
                    cryptoPriceRepository.getPrice(sym, cryptoSymCurrency).cryptoPriceData

                if (apiResponse != null) {
                    val newCryptoPrices = cryptoPrices.value!!.toMutableList()
                    newCryptoPrices.add(apiResponse)
                    cryptoPrices.postValue(newCryptoPrices)
//                    allCryptoSymbols += sym // added coin to fav list
                    cryptoSymbols.add(sym.uppercase()) // we can reuse default variable here
                    Log.d("CryptoList", stockSymbols.toString())

                    val cryptoSymbol = CryptoSymbol (
                        symbol = sym.uppercase(),
                        ownerName = currentUser.displayName ?: "Anonymous user",
                        ownerUid = currentUser.uid,
                    )

                    dbHelp.createCryptoymbols(cryptoSymbol, cryptoSymbolLiveList, currentUser)

                    respAlert.postValue(false)
                } else {
                    respAlert.postValue(true)
                }
            } else {
                respAlert.postValue(true)
            }
        }
    }
    fun netRefreshCrypto() {
        fetchCryptoDone.postValue(false)
        fetchCryptoLists()

        viewModelScope.launch {
            withContext(coroutineContext) {
                val content = arrayListOf<CryptoPriceData>()

                coroutineScope {
                    cryptoSymbols.forEach { sym ->
                        launch { // this will allow us to run multiple tasks in parallel
                            val apiResponse = cryptoPriceRepository.getPrice(sym,cryptoSymCurrency).cryptoPriceData
                            content.add(apiResponse)
                        }
                    }
                }  // coroutineScope block will wait here until all child tasks are completed
                cryptoPrices.postValue(content)
            }
        }
        fetchCryptoDone.postValue(true)
    }

    fun netRefreshNews(){
        viewModelScope.launch {
            viewModelScope.launch(
                context = viewModelScope.coroutineContext +
                        Dispatchers.IO) {
                val stockNewsData = stockNewsRepository.getNews(oneStockTicker)
                stockNews.postValue(stockNewsData)

            }
        }
    }
    // Public function to add stock/crypto
    fun addStock(stockTicker: String) {
        netStock(stockTicker)
    }
    fun addCrypto(cryptoTicker: String) {
        netCrypto(cryptoTicker)
    }

    // Function for one stock view
    fun oneStockTicker(newTicker: String, interval: String) {
        netOneStock(newTicker, interval)
        oneStockTicker = newTicker
    }

    // Observe Function
    fun observeStockTicker(): LiveData<List<StockData>>{
        return stockPrices
    }

    fun observeCryptoTicker(): LiveData<List<CryptoPriceData>>{
        return cryptoPrices
    }
    fun observeNews(): LiveData<List<StockNewsData>>{
        return stockNews
    }

    fun observeOneStock():LiveData<List<StockData>> {
        return oneStockPrice
    }

    fun observeFavStocks():LiveData<List<StockData>> {
        return favStockPrices
    }

    fun observeRespAlert():LiveData<Boolean?> {
        return respAlert
    }

    fun observeStockSymbolLiveList(): LiveData<List<StockSymbol>> {
        return stockSymbolLiveList
    }
    fun observeCryptoSymbolLiveList(): LiveData<List<CryptoSymbol>> {
        return cryptoSymbolLiveList
    }
    // Favorite functions
    fun addFavoriteStock(stockData: StockData) {
        var favList = mutableListOf<StockData>()
        if (favStockPrices.value != null) {
            favList = favStockPrices.value!!.toMutableList()
        }
        favList.add(stockData)
        favStockPrices.postValue(favList)
    }

    fun removeFavoriteStock(idx: Int) {
        if (favStockPrices.value != null) {
            var favList = favStockPrices.value!!.toMutableList()
            favList.removeAt(idx)
            favStockPrices.postValue(favList)
        }

    }

    // remove
    fun removeStockTickerAt(pos: Int, sym: String) {
        val currentUser = firebaseAuthLiveData.getCurrentUser()!!

        var stockPricesList = stockPrices.value!!.toMutableList()

        val stockToRemove = stockPricesList[pos]
        stockSymbols.remove(stockToRemove.symbol)
        stockPricesList.removeAt(pos)
        stockPrices.postValue(stockPricesList)
        var stockSymbolToDelete = StockSymbol()

        stockSymbolLiveList.value!!.forEach {
            if (it.symbol == sym && it.ownerUid == currentUser.uid) {
                stockSymbolToDelete = it
            }
        }

       dbHelp.removeStockSymbols(stockSymbolToDelete, stockSymbolLiveList, currentUser)
    }

    // remove
    fun removeCryptoTickerAt(pos: Int, sym: String) {
        val currentUser = firebaseAuthLiveData.getCurrentUser()!!

        var cryptoPricesList = cryptoPrices.value!!.toMutableList()

        val cryptoToRemove = cryptoPricesList[pos]
        cryptoSymbols.remove(cryptoToRemove.symbol)
        cryptoPricesList.removeAt(pos)
        cryptoPrices.postValue(cryptoPricesList)
        var cryptoSymbolToDelete = CryptoSymbol()

        cryptoSymbolLiveList.value!!.forEach {
            if (it.symbol == sym && it.ownerUid == currentUser.uid) {
                cryptoSymbolToDelete = it
            }
        }
        dbHelp.removeCryptoSymbols(cryptoSymbolToDelete, cryptoSymbolLiveList, currentUser)
    }

    // Fetch from Firebase DB
    fun fetchStockLists(){
        val currentUser = firebaseAuthLiveData.getCurrentUser()!!
        dbHelp.fetchStockSymbols(stockSymbolLiveList, currentUser)
    }
    fun getDefaultStockList(){
        val currentUser = firebaseAuthLiveData.getCurrentUser()!!
        defaultStockSymbols.forEach{
            Log.d("Empty DB collect", stockSymbolLiveList.toString())
            val stockSymbol = StockSymbol (
                symbol = it,
                ownerName = currentUser.displayName ?: "Anonymous user",
                ownerUid = currentUser.uid,
            )
            dbHelp.createStockSymbols(stockSymbol, stockSymbolLiveList, currentUser)
        }
    }
    fun getDBStockList(){
        stockSymbolLiveList.value!!.forEach {
            stockSymbols.add(it.symbol)
        }
    }

    fun fetchCryptoLists(){
        val currentUser = firebaseAuthLiveData.getCurrentUser()!!
        dbHelp.fetchCryptoSymbols(cryptoSymbolLiveList, currentUser)
    }

    fun getDefaultCryptoList(){
        val currentUser = firebaseAuthLiveData.getCurrentUser()!!

        defaultCryptoSymbols.forEach{
            Log.d("Empty DB collect", cryptoSymbolLiveList.toString())
            val cryptoSymbol = CryptoSymbol (
                symbol = it,
                ownerName = currentUser.displayName ?: "Anonymous user",
                ownerUid = currentUser.uid,
            )
            dbHelp.createCryptoymbols(cryptoSymbol, cryptoSymbolLiveList, currentUser)
        }
    }
    fun getDBCryptoList(){
         cryptoSymbolLiveList.value!!.forEach {
            cryptoSymbols.add(it.symbol)
        }
    }
    // Firebase update user
    fun updateUser() {
        firebaseAuthLiveData.updateUser()
        fetchStockLists()
        fetchCryptoLists()
    }

    // Convenient place to put it as it is shared
    companion object {
        fun doOneStock(context: Context, stockData: StockData) {
            val intent = Intent(context, OneStockActivity::class.java)
            intent.putExtra(OneStockActivity.symbolKey, stockData.symbol)
            context.startActivity(intent)
        }
    }
}