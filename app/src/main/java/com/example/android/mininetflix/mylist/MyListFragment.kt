package com.example.android.mininetflix.mylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.mininetflix.databinding.FragmentMylistBinding
import com.example.android.mininetflix.network.Movie
import com.example.android.mininetflix.overview.MoviePosterAdapter

// Sprint 9 — shows everything the user has saved via the heart on Detail.
// Re-uses MoviePosterAdapter (now serving 6 screens with one class).
class MyListFragment : Fragment() {

    private var _binding: FragmentMylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyListViewModel by lazy {
        ViewModelProvider(this)[MyListViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMylistBinding.inflate(inflater, container, false)

        val adapter = MoviePosterAdapter { movie -> openDetail(movie) }
        binding.mylistResults.adapter = adapter

        viewModel.favorites.observe(viewLifecycleOwner) { movies ->
            adapter.submitList(movies)
            // Empty state: hide the grid, show the "your list is empty" message.
            binding.mylistEmpty.visibility = if (movies.isEmpty()) View.VISIBLE else View.GONE
            binding.mylistResults.visibility = if (movies.isEmpty()) View.GONE else View.VISIBLE
        }

        return binding.root
    }

    private fun openDetail(movie: Movie) {
        findNavController().navigate(
            MyListFragmentDirections.actionMyListFragmentToDetailFragment(movie)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
