package com.example.android.mininetflix.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.mininetflix.databinding.FragmentSearchBinding
import com.example.android.mininetflix.network.Movie
import com.example.android.mininetflix.overview.MoviePosterAdapter
import com.example.android.mininetflix.overview.TmdbApiStatus

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by lazy {
        ViewModelProvider(this)[SearchViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Re-use MoviePosterAdapter for the 3-column results grid.
        val adapter = MoviePosterAdapter { movie -> openDetail(movie) }
        binding.searchResults.adapter = adapter

        // Every time the input text changes, tell the ViewModel. ViewModel handles debounce.
        binding.searchInput.doOnTextChanged { text, _, _, _ ->
            viewModel.onQueryChanged(text?.toString().orEmpty())
        }

        // Observe results → submit to adapter.
        viewModel.results.observe(viewLifecycleOwner) { adapter.submitList(it) }

        // Observe status → toggle the loading spinner.
        viewModel.status.observe(viewLifecycleOwner) { status ->
            binding.searchLoading.visibility =
                if (status == TmdbApiStatus.LOADING) View.VISIBLE else View.GONE
        }

        // Observe statusMessage → show "Type to search", "No movies found", or error text.
        viewModel.statusMessage.observe(viewLifecycleOwner) { msg ->
            binding.searchStatusText.text = msg
            binding.searchStatusText.visibility =
                if (msg.isNullOrBlank()) View.GONE else View.VISIBLE
        }

        return binding.root
    }

    private fun openDetail(movie: Movie) {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToDetailFragment(movie)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
