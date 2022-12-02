package com.example.uttradeataustin

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.example.uttradeataustin.databinding.ActivityMainBinding
import com.example.uttradeataustin.ui.crypto.CryptoHomeFragment
import com.example.uttradeataustin.ui.stock.HomeFragment
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private val stockFragTag = "stockFragTag"
    private val cryptoFragTag = "cryptoFragTag"
    private val viewModel: MainViewModel by viewModels()
    private var currentViewMode = "stock"
    private lateinit var activityMainBinding: ActivityMainBinding

    // Firebase authentication
    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) {
            viewModel.updateUser()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)


        supportFragmentManager.commit {
            add(R.id.main_frame, HomeFragment.newInstance(), stockFragTag)
        }

        supportActionBar?.setDisplayShowHomeEnabled(true)
        activityMainBinding.mainTv.text = "Stock Daily Watch"

        activityMainBinding.searchButton.setOnClickListener {
            if (currentViewMode == "stock") {
                if (viewModel.stockSymbols.contains(activityMainBinding.searchEt.text.toString().trim().uppercase())){
                    messageUser("Stock ticker already exists.")
                } else {
                    viewModel.addStock(activityMainBinding.searchEt.text.toString().trim().uppercase())
                }
            } else {
                if (viewModel.cryptoSymbols.contains(activityMainBinding.searchEt.text.toString().trim().uppercase())) {
                    messageUser("Crypto ticker already exists.")
                } else {
                    viewModel.addCrypto(activityMainBinding.searchEt.text.toString().trim().uppercase())
                }
            }

            hideKeyboard()
            activityMainBinding.searchEt.text.clear()
        }

        viewModel.observeRespAlert().observe(this,
            Observer{
                if (it == true)  {
                    messageUser("Ticker Not Found!")
                }
            })

        AuthInit(viewModel, signInLauncher)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.

        return when (item.itemId) {
            R.id.swtichViewButton -> { switchViewButton(item); true}
//            R.id.MyListButton -> { gotoMyList(item); true }
            R.id.logOutButton -> {
                viewModel.isStockInit = false
                viewModel.isCryptoInit = false
                signOut()
                true}
            android.R.id.home -> { false }
             // Handle in fragment
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun switchViewButton(@Suppress("UNUSED_PARAMETER") item: MenuItem) {
        activityMainBinding.searchEt.text.clear()
        if (item.title == "Crypto View"){
            supportFragmentManager.commit {
                replace(R.id.main_frame, CryptoHomeFragment.newInstance(), cryptoFragTag)
                setReorderingAllowed(false)
                addToBackStack(stockFragTag)
            }
            item.title = "Stock View"
            currentViewMode = "crypto"
            activityMainBinding.mainTv.text = "Crypto Realtime Watch"

        } else {
            supportFragmentManager.popBackStack()
            item.title = "Crypto View"
            currentViewMode = "stock"
            activityMainBinding.mainTv.text = "Stock Daily Watch"
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

    // An Android nightmare
    // https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    // https://stackoverflow.com/questions/7789514/how-to-get-activitys-windowtoken-without-view
    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)
    }

    private fun messageUser(message: String) {
        Snackbar.make(activityMainBinding.mainLayout,
            message, Snackbar.LENGTH_LONG).show()
    }
}