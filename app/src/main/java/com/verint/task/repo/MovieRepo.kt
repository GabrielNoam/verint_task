package com.verint.task.repo

import androidx.lifecycle.LiveData
import com.verint.task.api.Resource
import com.verint.task.model.Movie
import com.verint.task.model.Search

/**
 * MovieRepo
 *
 * @author Gabriel Noam
 */
interface MovieRepo {
    fun get(search: Search): LiveData<Resource<List<Movie>>>
}