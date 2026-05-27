package com.example.android.mininetflix.network

// One entry from the TMDB `movie/{id}/videos` response. We only keep the fields
// we actually use; Moshi silently drops the rest.
data class Video(
    val id: String,
    val key: String,       // YouTube video ID — what we put after ?v= in the URL
    val name: String,      // e.g. "Official Trailer"
    val site: String,      // we filter for "YouTube"
    val type: String,      // we filter for "Trailer"
    val official: Boolean
)
