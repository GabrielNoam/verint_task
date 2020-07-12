package com.verint.task.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
/**
 * Movie
 *
 * @author Gabriel Noam
 */
@Entity
data class Movie (

    @ColumnInfo(name = "title")
    @SerializedName("Title")
    var title: String,

    @ColumnInfo(name = "year")
    @SerializedName("Year")
    var year: String,

    @PrimaryKey
    @ColumnInfo(name = "imdbID")
    @SerializedName("imdbID")
    var imdbID: String,

    @ColumnInfo(name = "poster")
    @SerializedName("Poster")
    var poster: String,

    @ColumnInfo(name = "key")
    var key: String,

    @ColumnInfo(name = "page")
    var page: Int)