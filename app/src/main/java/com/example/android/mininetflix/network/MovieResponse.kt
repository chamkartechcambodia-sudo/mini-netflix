package com.example.android.mininetflix.network

import com.squareup.moshi.Json

// Matches the outer JSON object: { "page": ..., "results": [ ... ], "total_pages": ... }
data class MovieResponse(
    val page: Int,
    val results: List<Movie>,
    @Json(name = "total_pages") val totalPages: Int
)