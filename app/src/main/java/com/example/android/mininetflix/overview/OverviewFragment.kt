package com.example.android.mininetflix.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.mininetflix.databinding.FragmentOverviewBinding
import com.example.android.mininetflix.network.Movie

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

        // Shared click handler for every poster + the hero card: navigate to Detail.
        val onMovieClick: (Movie) -> Unit = { movie ->
            findNavController().navigate(
                OverviewFragmentDirections.actionOverviewFragmentToDetailFragment(movie)
            )
        }

        // 4 adapters, one per row — all reuse MoviePosterAdapter (no new adapter class needed).
        val popularAdapter = MoviePosterAdapter(onMovieClick)
        val topRatedAdapter = MoviePosterAdapter(onMovieClick)
        val nowPlayingAdapter = MoviePosterAdapter(onMovieClick)
        val upcomingAdapter = MoviePosterAdapter(onMovieClick)

        // Wire each RecyclerView with a HORIZONTAL LinearLayoutManager + its adapter.
        setupRow(binding.popularRow, popularAdapter)
        setupRow(binding.topRatedRow, topRatedAdapter)
        setupRow(binding.nowPlayingRow, nowPlayingAdapter)
        setupRow(binding.upcomingRow, upcomingAdapter)

        // Observe the 4 LiveData — each emits once the parallel fetch finishes.
        viewModel.popular.observe(viewLifecycleOwner) { popularAdapter.submitList(it) }
        viewModel.topRated.observe(viewLifecycleOwner) { topRatedAdapter.submitList(it) }
        viewModel.nowPlaying.observe(viewLifecycleOwner) { nowPlayingAdapter.submitList(it) }
        viewModel.upcoming.observe(viewLifecycleOwner) { upcomingAdapter.submitList(it) }

        // Hero: load the featured movie (first item of Popular) into the big card.
        viewModel.featured.observe(viewLifecycleOwner) { movie ->
            if (movie != null) {
                binding.heroTitle.text = movie.title
                val imagePath = movie.backdropPath ?: movie.posterPath
                if (imagePath != null) {
                    Glide.with(binding.heroImage.context)
                        .load("https://image.tmdb.org/t/p/w780$imagePath")
                        .into(binding.heroImage)
                }
                binding.heroContainer.setOnClickListener { onMovieClick(movie) }
            }
        }

        // Status: show spinner / error icon; hide the scroll content while loading or on error.
        viewModel.statusMessage.observe(viewLifecycleOwner) { msg ->
            binding.statusText.text = msg
        }
        viewModel.status.observe(viewLifecycleOwner) { status ->
            binding.loadingSpinner.visibility =
                if (status == TmdbApiStatus.LOADING) View.VISIBLE else View.GONE
            binding.errorImage.visibility =
                if (status == TmdbApiStatus.ERROR) View.VISIBLE else View.GONE
            binding.scrollContainer.visibility =
                if (status == TmdbApiStatus.DONE) View.VISIBLE else View.GONE
        }

        return binding.root
    }

    // Small helper: one line per row in onCreateView.
    private fun setupRow(rv: RecyclerView, adapter: MoviePosterAdapter) {
        rv.adapter = adapter
        rv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
