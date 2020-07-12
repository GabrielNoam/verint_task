package com.verint.task.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.verint.task.database.MovieDatabase.Companion.MAX_DB_SIZE
import com.verint.task.model.Movie

/**
 * MovieDao
 *
 * @author Gabriel Noam
 */

@Dao
interface MovieDao {
    @Query("SELECT COUNT(*) FROM movie")
    fun getCount(): Int

    @Query("SELECT * FROM movie")
    fun loadAll(): LiveData<List<Movie>>

    @Query("SELECT * FROM movie")
    fun getAll(): List<Movie>

    @Query("SELECT * FROM movie WHERE (movie.`key` = :key AND movie.page = :page)")
    fun search(key: String, page: Int): LiveData<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(movie: Movie): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertList(movies: List<Movie>): LongArray

    @Query("DELETE FROM movie")
    suspend fun delete()

    @Transaction
    suspend fun deleteAndInsert(
        searchDao: SearchDao,
        movies: List<Movie>,
        delete: Boolean = true,
        maxDbSize: Int = MAX_DB_SIZE): LongArray
    {
        if(delete && getCount() > maxDbSize) {
            searchDao.delete()
            delete()

        }

        return insertList(movies)
    }
}