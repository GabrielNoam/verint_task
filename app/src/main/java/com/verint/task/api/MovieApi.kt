package com.verint.task.api

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.verint.task.model.Movies
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * MovieApi
 *
 * @author Gabriel Noam
 */
interface MovieApi {
    companion object {
        const val FORMAT = "json"
        const val API_KEY = "f972a33e"
        const val BASE_URL = "https://www.omdbapi.com/"

        fun openMovie(context: Context, imdbID: String) {
            val url = "$BASE_URL?apikey=$API_KEY&plot=full&i=$imdbID"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context.startActivity(intent)
        }
    }

    @GET("./")
    suspend fun getMovies(@Query("r") format: String, @Query("apikey") apikey: String, @Query("s", encoded = true) key: String, @Query("page") page: Int): Movies
}