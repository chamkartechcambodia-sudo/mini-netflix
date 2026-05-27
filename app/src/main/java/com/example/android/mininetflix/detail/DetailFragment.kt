package com.example.android.mininetflix.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.android.mininetflix.R
import com.example.android.mininetflix.databinding.FragmentDetailBinding
import com.example.android.mininetflix.mylist.data.AppDatabase

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    // SafeArgs: the Movie passed from OverviewFragment.
    private val args: DetailFragmentArgs by navArgs()

    // Sprint 6 — owns the trailer fetch so it survives rotation.
    private val viewModel: DetailViewModel by lazy {
        ViewModelProvider(this)[DetailViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        val movie = args.selectedMovie

        binding.detailTitle.text = movie.title
        binding.detailRating.text = "★ %.1f".format(movie.voteAverage)
        binding.detailOverview.text = movie.overview

        // Show only the year (first 4 chars of "2026-04-15"); hide the dot if there is no date.
        val year = movie.releaseDate?.take(4)
        binding.detailRelease.text = year ?: ""
        binding.detailDot.visibility = if (year.isNullOrBlank()) View.GONE else View.VISIBLE

        // Prefer the wide backdrop; fall back to the poster.
        val imagePath = movie.backdropPath ?: movie.posterPath
        if (imagePath != null) {
            Glide.with(binding.detailImage.context)
                .load("https://image.tmdb.org/t/p/w780$imagePath")
                .into(binding.detailImage)
        }

        // Sprint 6 — trailer. Both the big red button and the centered hero icon share
        // the same handler and visibility (shown only when a trailer exists).
        viewModel.fetchTrailer(movie.id)
        viewModel.trailerKey.observe(viewLifecycleOwner) { key ->
            val visible = if (key != null) View.VISIBLE else View.GONE
            binding.playTrailerButton.visibility = visible
            binding.heroPlayButton.visibility = visible
            if (key != null) {
                val openTrailer = View.OnClickListener {
                    val url = "https://www.youtube.com/watch?v=$key".toUri()
                    startActivity(Intent(Intent.ACTION_VIEW, url))
                }
                binding.playTrailerButton.setOnClickListener(openTrailer)
                binding.heroPlayButton.setOnClickListener(openTrailer)
            }
        }

        // Sprint 6.5 — Share: open the system share sheet with title + TMDB link.
        binding.shareButton.setOnClickListener {
            val shareText = "${movie.title} — https://www.themoviedb.org/movie/${movie.id}"
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, movie.title)
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(sendIntent, "Share movie via"))
        }

        // Sprint 9 — My List heart toggle, backed by Room.
        val dao = AppDatabase.getInstance(requireContext()).favoriteDao()
        viewModel.checkFavorite(dao, movie.id)
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFav ->
            val iconRes = if (isFav) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
            binding.myListIcon.setImageResource(iconRes)
        }
        binding.myListButton.setOnClickListener {
            // Sprint 10 — bounce the heart for tactile feedback (scale up then back).
            binding.myListIcon.animate()
                .scaleX(1.3f).scaleY(1.3f)
                .setDuration(100)
                .withEndAction {
                    binding.myListIcon.animate()
                        .scaleX(1f).scaleY(1f)
                        .setDuration(100)
                        .start()
                }.start()
            viewModel.toggleFavorite(dao, movie)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
