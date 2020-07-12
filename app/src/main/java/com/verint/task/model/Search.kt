package com.verint.task.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Search
 *
 * @author Gabriel Noam
 */
@Entity
data class Search (
    @ColumnInfo(name = "key")
    var key: String,

    @ColumnInfo(name = "page")
    var page: Int,

    @PrimaryKey
    @ColumnInfo(name = "timestamp")
    var timestamp: Long)