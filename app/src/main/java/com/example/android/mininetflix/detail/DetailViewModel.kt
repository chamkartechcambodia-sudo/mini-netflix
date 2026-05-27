package com.example.android.mininetflix.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.mininetflix.BuildConfig
import com.example.android.mininetflix.mylist.data.FavoriteDao
import com.example.android.mininetflix.mylist.data.FavoriteMovie
import com.example.android.mininetflix.network.Movie
import com.example.android.mininetflix.network.TmdbApi
import kotlinx.coroutines.launch

// Owns the "secondary fetch" for the Detail screen (trailer) AND the favorite toggle.
class DetailViewModel : ViewModel() {

    // --- Sprint 6 — trailer ---

    private val _trailerKey = MutableLiveData<String?>(null)
    val trailerKey: LiveData<String?> = _trailerKey

    private var hasFetched = false

    fun fetchTrailer(movieId: Int) {
        if (hasFetched) return
        hasFetched = true

        viewModelScope.launch {
            try {
                val response = TmdbApi.retrofitService.getMovieVideos(
                    movieId = movieId,
                    apiKey = BuildConfig.TMDB_API_KEY
                )
                val trailer = response.results.firstOrNull {
                    it.site == "YouTube" && it.type == "Trailer" && it.official
                } ?: response.results.firstOrNull {
                    it.site == "YouTube" && it.type == "Trailer"
                }
                _trailerKey.value = trailer?.key
            } catch (e: Exception) {
                _trailerKey.value = null
            }
        }
    }

    // --- Sprint 9 — favorite (My List) ---

    private val _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> = _isFavorite

    // Called once when Detail opens — tells us whether this movie is already saved.
    fun checkFavorite(dao: FavoriteDao, movieId: Int) {
        viewModelScope.launch {
            _isFavorite.value = dao.exists(movieId)
        }
    }

    // Heart icon tapped — flip the state in DB + LiveData.
    fun toggleFavorite(dao: FavoriteDao, movie: Movie) {
        viewModelScope.launch {
            val currentlyFav = _isFavorite.value == true
            if (currentlyFav) {
                dao.delete(movie.id)
                _isFavorite.value = false
            } else {
                dao.insert(FavoriteMovie.fromMovie(movie))
                _isFavorite.value = true
            }
        }
    }
}
