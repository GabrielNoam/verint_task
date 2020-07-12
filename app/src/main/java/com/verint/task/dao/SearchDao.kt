package com.verint.task.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.verint.task.model.Search

/**
 * SearchDao
 *
 * @author Gabriel Noam
 */

@Dao
interface SearchDao {
    @Query("SELECT COUNT(*) FROM search")
    fun getCount(): Int

    @Query("SELECT * FROM search WHERE timestamp = :timestamp LIMIT 1")
    fun get(timestamp: Long): LiveData<Search?>

    @Query("SELECT max(timestamp) FROM search")
    fun max(): Long?

    fun last(): LiveData<Search?> {
        val timestamp = max()
        return if(timestamp == null)
            MutableLiveData(null)
        else
            get(timestamp)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(search: Search): Long

    @Query("DELETE FROM search")
    suspend fun delete()
}