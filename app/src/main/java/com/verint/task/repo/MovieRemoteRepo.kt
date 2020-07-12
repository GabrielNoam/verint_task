package com.verint.task.repo

import androidx.lifecycle.liveData
import com.google.gson.GsonBuilder
import com.verint.task.api.MovieApi
import com.verint.task.api.MovieApi.Companion.API_KEY
import com.verint.task.api.MovieApi.Companion.BASE_URL
import com.verint.task.api.MovieApi.Companion.FORMAT
import com.verint.task.api.Resource
import com.verint.task.api.ResponseHandler
import com.verint.task.model.Movie
import com.verint.task.model.Movies.Companion.FAIL
import com.verint.task.model.Movies.Companion.SUCCESS
import com.verint.task.model.Search
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * MovieRemoteRepo
 *
 * @author Gabriel Noam
 */
internal class MovieRemoteRepo : MovieRepo {

    private val responseHandler = ResponseHandler()

    private val movieAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build().create(MovieApi::class.java)
    }

    override fun get(search: Search) = liveData { emit(fetch(search.key, search.page)) }

    private suspend fun fetch(key: String, page: Int): Resource<List<Movie>> {
        return try {

            val movies = movieAPI.getMovies(FORMAT, API_KEY, key.trim().replace(" ","%20"), page)

            when (movies.status) {
                SUCCESS -> responseHandler.handleSuccess(
                    movies.result.mapIndexed { _, movie -> movie.apply {
                        movie.key = key
                        movie.page = page
                    }}, movies.count.toIntOrNull()?: 0)

                FAIL -> responseHandler.handleSuccess(ArrayList(), 0)
                else -> responseHandler.handleException(Exception(movies.status))
            }
        } catch (e: Exception) {
            responseHandler.handleException(e)
        }
    }
}