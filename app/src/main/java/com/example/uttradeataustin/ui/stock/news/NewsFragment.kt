package com.example.uttradeataustin.ui.stock.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uttradeataustin.databinding.FragmentRvBinding
import com.example.uttradeataustin.MainViewModel

class NewsFragment: Fragment() {
    // XXX initialize viewModel
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }
    private var _binding: FragmentRvBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): NewsFragment {
            return NewsFragment()
        }
    }

    // Set up the adapter
    private fun initAdapter(binding: FragmentRvBinding) : NewsRowAdapter {
        val adapter = NewsRowAdapter(viewModel)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(binding.recyclerView.context)

        viewModel.observeNews().observe(viewLifecycleOwner,
            Observer{
                adapter.submitList(it.toMutableList())
                adapter.notifyDataSetChanged()
                Log.d("data", it[0].title)
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
        initAdapter(binding)
    }
}