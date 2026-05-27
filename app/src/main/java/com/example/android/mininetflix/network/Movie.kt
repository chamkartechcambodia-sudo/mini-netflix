package com.example.android.mininetflix.network

import com.squareup.moshi.Json
import java.io.Serializable

// One movie from the TMDB response.
// Serializable lets us pass a Movie between screens later (via SafeArgs).
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    @Json(name = "poster_path") val posterPath: String?,
    @Json(name = "backdrop_path") val backdropPath: String?,
    @Json(name = "vote_average") val voteAverage: Double,
    @Json(name = "release_date") val releaseDate: String?
) : Serializable