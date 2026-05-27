package com.example.android.mininetflix.mylist.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.mininetflix.network.Movie

// Sprint 9 — Room entity for a movie the user has saved to "My List".
// We copy fields from Movie so My List works offline (no network needed to re-render).
@Entity(tableName = "favorite_movies")
data class FavoriteMovie(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val releaseDate: String?
) {
    // Convert back to a network Movie so we can re-use the Detail/grid code paths.
    fun toMovie(): Movie = Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        releaseDate = releaseDate
    )

    companion object {
        fun fromMovie(m: Movie): FavoriteMovie = FavoriteMovie(
            id = m.id,
            title = m.title,
            overview = m.overview,
            posterPath = m.posterPath,
            backdropPath = m.backdropPath,
            voteAverage = m.voteAverage,
            releaseDate = m.releaseDate
        )
    }
}
