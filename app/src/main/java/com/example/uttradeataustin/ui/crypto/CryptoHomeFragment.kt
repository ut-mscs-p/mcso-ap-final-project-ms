package com.example.uttradeataustin.ui.crypto

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.uttradeataustin.databinding.CryptoFragmentRvBinding
import com.example.uttradeataustin.MainViewModel

class CryptoHomeFragment: Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: CryptoFragmentRvBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): CryptoHomeFragment {
            return CryptoHomeFragment()
        }
    }

    private fun initSwipeLayout(swipe : SwipeRefreshLayout) {
        swipe.setOnRefreshListener {
            viewModel.netRefreshCrypto()
        }
        viewModel.fetchCryptoDone.observe(requireActivity()) {
            swipe.isRefreshing = false
        }
    }
    // Set up the adapter
    private fun initAdapter(binding: CryptoFragmentRvBinding) : CryptoTickerRowAdapter {
        val adapter = CryptoTickerRowAdapter(viewModel)
        binding.cryptoRecyclerView.adapter = adapter
        binding.cryptoRecyclerView.layoutManager = LinearLayoutManager(binding.cryptoRecyclerView.context)

        viewModel.observeCryptoTicker().observe(viewLifecycleOwner,
            Observer{
                adapter.submitList(it.toMutableList())
                adapter.notifyDataSetChanged()
            })

        return adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CryptoFragmentRvBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")

        viewModel.observeCryptoSymbolLiveList().observe(viewLifecycleOwner
        ) {
            if (!viewModel.isCryptoInit) {
                if (it.isEmpty()) {
                    viewModel.getDefaultCryptoList()
                } else {
                    viewModel.isCryptoInit = true
                }
                viewModel.getDBCryptoList()
                viewModel.netRefreshCrypto()
            }

        }
        initAdapter(binding)
        initSwipeLayout(binding.CryptoSwipeRefreshLayout)

    }
}