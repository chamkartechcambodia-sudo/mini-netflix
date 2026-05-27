package com.example.android.mininetflix.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.mininetflix.databinding.FragmentOverviewBinding

class OverviewFragment : Fragment() {

    // View Binding — valid only between onCreateView and onDestroyView.
    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OverviewViewModel by lazy {
        ViewModelProvider(this)[OverviewViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)

        // When a poster is tapped, navigate to the detail screen, passing the movie.
        val adapter = MoviePosterAdapter { movie ->
            findNavController().navigate(
                OverviewFragmentDirections.actionOverviewFragmentToDetailFragment(movie)
            )
        }
        binding.photosGrid.adapter = adapter

        viewModel.movies.observe(viewLifecycleOwner) { movies ->
            adapter.submitList(movies)
        }

        viewModel.statusMessage.observe(viewLifecycleOwner) { message ->
            binding.statusText.text = message
        }

        viewModel.status.observe(viewLifecycleOwner) { status ->
            binding.loadingSpinner.visibility =
                if (status == TmdbApiStatus.LOADING) View.VISIBLE else View.GONE
            binding.errorImage.visibility =
                if (status == TmdbApiStatus.ERROR) View.VISIBLE else View.GONE
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
