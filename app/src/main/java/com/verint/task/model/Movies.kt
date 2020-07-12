package com.verint.task.model

import com.google.gson.annotations.SerializedName
/**
 * Movies
 *
 * @author Gabriel Noam
 */
data class Movies (@SerializedName("Response")
                   var status: String,
                   @SerializedName("totalResults")
                   var count: String,
                   @SerializedName("Search")
                   var result: ArrayList<Movie>) {
    companion object {
        const val SUCCESS = "True"
        const val FAIL = "False"
    }
}
