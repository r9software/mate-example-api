package com.example.sitemateexampleapi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.sitemateexampleapi.databinding.FragmentFirstBinding
import com.example.sitemateexampleapi.network.Network
import kotlinx.coroutines.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    val errorHandler = CoroutineExceptionHandler { context, error ->
        coroutineScope.launch(Dispatchers.Main) {
            binding.searchResult.text = error.message?: getString(R.string.error)
        }
    }
    private lateinit var coroutineScope: CoroutineScope
    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchText.doOnTextChanged { text, start, before, count ->
            parseAndSearch()
        }
        binding.searchButton.setOnClickListener {
            parseAndSearch()
        }
    }

    private fun parseAndSearch() {
        val text = binding.searchText.text
        val result = text.split(",")
        if (result.size < 2 || result[1].isEmpty() || result[1].trim().length < 2)
            binding.searchResult.text = getString(R.string.error_search)
        else
            fetchData(result[0].trim(), result[1].trim())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Launch approach
    fun fetchData(artist: String, song: String) {
        coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        coroutineScope.launch(errorHandler) {
            Log.d("Log", "Fetch artist/song")
            val result = Network.fetchHttp(artist, song)
            launch(Dispatchers.Main) {
                binding.searchResult.text = result
            }
        }
    }

}