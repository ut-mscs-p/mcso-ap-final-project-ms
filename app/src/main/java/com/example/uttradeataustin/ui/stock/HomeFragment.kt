package com.example.uttradeataustin.ui.stock

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
import com.example.uttradeataustin.MainViewModel
import com.example.uttradeataustin.databinding.FragmentRvBinding

class HomeFragment: Fragment() {
    // initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentRvBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    private fun initSwipeLayout(swipe : SwipeRefreshLayout) {
        swipe.setOnRefreshListener {
            viewModel.netRefreshStock()
        }
        viewModel.fetchStockDone.observe(requireActivity()) {
            swipe.isRefreshing = false
        }
    }

    // Set up the adapter
    private fun initAdapter(binding: FragmentRvBinding) : TickerRowAdapter {
        val adapter = TickerRowAdapter(viewModel)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(binding.recyclerView.context)

        viewModel.observeStockTicker().observe(viewLifecycleOwner,
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
        _binding = FragmentRvBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        viewModel.observeStockSymbolLiveList().observe(viewLifecycleOwner
        ) {

            if (!viewModel.isStockInit) {
                if (it.isEmpty()) {
                    viewModel.getDefaultStockList()
                } else {
                    viewModel.isStockInit = true
                }
                viewModel.getDBStockList()
                viewModel.netRefreshStock()
            }

        }
        initAdapter(binding)
        initSwipeLayout(binding.swipeRefreshLayout)

    }
}