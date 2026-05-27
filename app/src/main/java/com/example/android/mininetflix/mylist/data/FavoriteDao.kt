package com.example.android.mininetflix.mylist.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// Sprint 9 — Room DAO for the favorite_movies table.
// observeAll() / observeIsFavorite() return LiveData → the UI auto-updates when the
// database changes; no manual refresh needed.
@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite_movies ORDER BY id DESC")
    fun observeAll(): LiveData<List<FavoriteMovie>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_movies WHERE id = :movieId)")
    suspend fun exists(movieId: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: FavoriteMovie)

    @Query("DELETE FROM favorite_movies WHERE id = :movieId")
    suspend fun delete(movieId: Int)
}
