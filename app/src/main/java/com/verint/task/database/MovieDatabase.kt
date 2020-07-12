package com.verint.task.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.verint.task.dao.MovieDao
import com.verint.task.dao.SearchDao
import com.verint.task.model.Movie
import com.verint.task.model.Search
import com.verint.task.singleton

/**
 * MovieDatabase
 *
 * @author Gabriel Noam
 */
@Database(entities = [Movie::class, Search::class], version = 4)
abstract class MovieDatabase : RoomDatabase() {

    companion object {
        const val MAX_DB_SIZE = 100000

        private const val DB_NAME = "movie"
        private var db: MovieDatabase? = null

        fun getInstance(context: Context): MovieDatabase =
            db.singleton(this) {
                buildDatabase(context).also { db = it}
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context, MovieDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }

    abstract fun movieDao(): MovieDao
    abstract fun searchDao(): SearchDao
}