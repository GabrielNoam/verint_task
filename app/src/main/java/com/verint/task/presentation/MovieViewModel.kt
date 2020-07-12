package com.verint.task.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.verint.task.model.Search
import com.verint.task.repo.MovieRepoImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
/**
 * MovieViewModel
 *
 * @author Gabriel Noam
 */
class MovieViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MovieRepoImpl(application)

    suspend fun retrieveMovies() = withContext(Dispatchers.IO) {repository.retrieve()}

    fun getMovies(search: Search) = repository.get(search)
}