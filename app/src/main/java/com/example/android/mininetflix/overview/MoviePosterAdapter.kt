package com.example.android.mininetflix.overview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.mininetflix.databinding.GridViewItemBinding
import com.example.android.mininetflix.network.Movie

class MoviePosterAdapter(private val onClick: (Movie) -> Unit) :
    ListAdapter<Movie, MoviePosterAdapter.MovieViewHolder>(DiffCallback) {

    // Holds the views for ONE grid cell (uses View Binding).
    class MovieViewHolder(private val binding: GridViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: Movie) {
            binding.moviePoster.contentDescription = movie.title
            val posterPath = movie.posterPath
            if (posterPath != null) {
                // Build the full TMDB image URL and load it with Glide.
                val fullUrl = "https://image.tmdb.org/t/p/w342$posterPath"
                Glide.with(binding.moviePoster.context)
                    .load(fullUrl)
                    .error(android.R.drawable.stat_notify_error)
                    .into(binding.moviePoster)
            } else {
                binding.moviePoster.setImageDrawable(null)
            }
        }
    }

    // Lets ListAdapter detect which items changed.
    companion object DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie) = oldItem == newItem
    }

    // Creates a new empty cell.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(
            GridViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    // Fills a cell with the movie at this position.
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.itemView.setOnClickListener { onClick(movie) }
        holder.bind(movie)
    }
}
