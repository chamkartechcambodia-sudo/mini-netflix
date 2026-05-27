package com.example.android.mininetflix.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.android.mininetflix.BuildConfig
import com.example.android.mininetflix.network.Movie
import com.example.android.mininetflix.network.TmdbApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

// The three states a network request can be in.
enum class TmdbApiStatus { LOADING, ERROR, DONE }

class OverviewViewModel : ViewModel() {

    // Status of the parallel-fetch operation.
    private val _status = MutableLiveData<TmdbApiStatus>()
    val status: LiveData<TmdbApiStatus> get() = _status

    // A friendly message shown while loading or on error (empty when DONE).
    val statusMessage: LiveData<String> = status.map { state ->
        when (state) {
            TmdbApiStatus.LOADING -> "Loading movies…"
            TmdbApiStatus.ERROR -> "⚠ Couldn't load movies.\nCheck your Internet connection."
            else -> ""
        }
    }

    // Sprint 7 — one LiveData per Netflix-style row.
    private val _popular = MutableLiveData<List<Movie>>()
    val popular: LiveData<List<Movie>> get() = _popular

    private val _topRated = MutableLiveData<List<Movie>>()
    val topRated: LiveData<List<Movie>> get() = _topRated

    private val _nowPlaying = MutableLiveData<List<Movie>>()
    val nowPlaying: LiveData<List<Movie>> get() = _nowPlaying

    private val _upcoming = MutableLiveData<List<Movie>>()
    val upcoming: LiveData<List<Movie>> get() = _upcoming

    // Featured movie shown in the hero card at the top — first item of Popular.
    private val _featured = MutableLiveData<Movie?>()
    val featured: LiveData<Movie?> get() = _featured

    // Fetch immediately when the ViewModel is created.
    init {
        loadHome()
    }

    private fun loadHome() {
        viewModelScope.launch {
            _status.value = TmdbApiStatus.LOADING
            try {
                val key = BuildConfig.TMDB_API_KEY

                // Wrap the parallel `async` calls in `coroutineScope { }`.
                //
                // Without this wrapper, if any one async child fails (e.g. offline →
                // IOException), the failure propagates straight up to `viewModelScope.launch`'s
                // Job — even though our try/catch catches the rethrown IOException, the
                // parent Job is left in a "failed" state and the uncaught exception handler
                // still fires → APP CRASHES when offline. This is a classic Kotlin coroutines
                // footgun. `coroutineScope { }` CONTAINS the exception inside its own scope:
                // catch handles it, parent launch stays healthy, no crash.
                coroutineScope {
                    val popularDeferred    = async { TmdbApi.retrofitService.getPopular(key) }
                    val topRatedDeferred   = async { TmdbApi.retrofitService.getTopRated(key) }
                    val nowPlayingDeferred = async { TmdbApi.retrofitService.getNowPlaying(key) }
                    val upcomingDeferred   = async { TmdbApi.retrofitService.getUpcoming(key) }

                    val popular    = popularDeferred.await().results
                    val topRated   = topRatedDeferred.await().results
                    val nowPlaying = nowPlayingDeferred.await().results
                    val upcoming   = upcomingDeferred.await().results

                    _popular.value    = popular
                    _topRated.value   = topRated
                    _nowPlaying.value = nowPlaying
                    _upcoming.value   = upcoming
                    _featured.value   = popular.firstOrNull()
                }
                _status.value = TmdbApiStatus.DONE
            } catch (e: Exception) {
                _popular.value    = emptyList()
                _topRated.value   = emptyList()
                _nowPlaying.value = emptyList()
                _upcoming.value   = emptyList()
                _featured.value   = null
                _status.value = TmdbApiStatus.ERROR
            }
        }
    }
}
