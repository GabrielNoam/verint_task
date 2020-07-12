package com.verint.task.repo

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.verint.task.api.Resource
import com.verint.task.database.MovieDatabase
import com.verint.task.model.Movie
import com.verint.task.model.Search
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * MovieLocalRepo
 *
 * @author Gabriel Noam
 */
internal class MovieLocalRepo(application: Application): MovieRepo {
    private val database = MovieDatabase.getInstance(application.applicationContext)

    private val movieDao  = database.movieDao()
    private val searchDao = database.searchDao()

    private val emptyResourceLiveData =
            MutableLiveData<Resource<List<Movie>>>(Resource.local(mutableListOf()))

     fun retrieve() =
         searchDao.last().switchMap { if(it == null) emptyResourceLiveData else search(it) }

    override fun get(search: Search): LiveData<Resource<List<Movie>>> {

        // This operation may lock the UI thread, therefore using Dispatchers.IO.
        // The insert is not required till retrieve or pagination and no need to wait for its to end
        GlobalScope.launch(Dispatchers.IO) { searchDao.insert(search)  }

        return movieDao.search(search.key, search.page).map {
            Resource.local(it)
        }
    }

    private fun search(search: Search): LiveData<Resource<List<Movie>>> =
        movieDao.search(search.key, search.page).map {
            Resource.local(it)
        }


    suspend fun set(movies: List<Movie>) =
        movieDao.deleteAndInsert(searchDao, movies)
}