package com.example.android.mininetflix.mylist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.android.mininetflix.mylist.data.AppDatabase
import com.example.android.mininetflix.network.Movie

// Sprint 9 — owns the My List screen. Reads the favorites table via Room, exposes a
// LiveData<List<Movie>> so the existing MoviePosterAdapter can render the grid.
// AndroidViewModel gets Application from the framework — no factory needed for this case.
class MyListViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).favoriteDao()

    // Convert FavoriteMovie → Movie so we can re-use the regular Adapter / Detail flow.
    val favorites: LiveData<List<Movie>> = dao.observeAll().map { list ->
        list.map { it.toMovie() }
    }
}
