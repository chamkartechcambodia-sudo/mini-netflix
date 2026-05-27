package com.example.android.mininetflix.network

// Wrapper for the `movie/{id}/videos` endpoint:
// { "id": 12345, "results": [ Video, Video, ... ] }
data class VideoResponse(
    val id: Int,
    val results: List<Video>
)
