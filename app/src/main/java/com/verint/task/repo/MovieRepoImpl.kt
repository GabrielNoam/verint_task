package com.verint.task.repo

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.verint.task.api.Resource
import com.verint.task.model.Movie
import com.verint.task.model.Search
import com.verint.task.model.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * MovieRepoImpl
 *
 * @author Gabriel Noam
 */
class MovieRepoImpl(application: Application) : MovieRepo {
    private val local = MovieLocalRepo(application)
    private val remote = MovieRemoteRepo()

    fun retrieve(): LiveData<Resource<List<Movie>>> = local.retrieve()

    override fun get(search: Search): LiveData<Resource<List<Movie>>> {
        var liveDataMerger: MediatorLiveData<Resource<List<Movie>>> = MediatorLiveData()
        val localResourceLiveData: LiveData<Resource<List<Movie>>> = local.get(search)

        liveDataMerger.addSource(localResourceLiveData) {
            if (it.data != null && it.data.isNotEmpty()) liveDataMerger.postValue(it)
            else load(liveDataMerger, search)
        }

        return liveDataMerger
    }

    private fun load(liveDataMerger: MediatorLiveData<Resource<List<Movie>>>, search: Search) {

        val remoteResourceLiveData: LiveData<Resource<List<Movie>>> = remote.get(search)
        liveDataMerger.addSource(remoteResourceLiveData) { result ->
            if (result.status == Status.REMOTE) result.data?.let {
                GlobalScope.launch(Dispatchers.IO) {
                    local.set(result.data)
                }
                liveDataMerger.removeSource(remoteResourceLiveData)
                liveDataMerger.postValue(result)
            }
        }
    }
}


