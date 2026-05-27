package com.example.android.mininetflix.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.mininetflix.BuildConfig
import com.example.android.mininetflix.network.TmdbApi
import kotlinx.coroutines.launch

// Owns the "secondary fetch" for the Detail screen: given a movie ID, ask TMDB
// for that movie's videos and expose the YouTube trailer key (if any).
//
// Why a ViewModel? It survives screen rotation, so we don't hit the network
// again every time the user rotates the device.
class DetailViewModel : ViewModel() {

    // The YouTube key of the trailer to play. null = "no trailer available
    // (either not fetched yet, or none was found)".
    private val _trailerKey = MutableLiveData<String?>(null)
    val trailerKey: LiveData<String?> = _trailerKey

    // Make sure we only hit the network once for this movie, even if onCreateView
    // runs again (e.g. after a rotation).
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
                // Prefer an OFFICIAL YouTube Trailer; if none, take any YouTube Trailer.
                val trailer = response.results.firstOrNull {
                    it.site == "YouTube" && it.type == "Trailer" && it.official
                } ?: response.results.firstOrNull {
                    it.site == "YouTube" && it.type == "Trailer"
                }
                _trailerKey.value = trailer?.key
            } catch (e: Exception) {
                // Network error — just leave the button hidden. No need to alarm
                // the user; the rest of the Detail screen still works.
                _trailerKey.value = null
            }
        }
    }
}
