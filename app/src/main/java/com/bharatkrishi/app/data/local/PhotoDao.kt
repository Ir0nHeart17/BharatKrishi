package com.bharatkrishi.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PhotoDao {
    @Insert
    suspend fun insert(photo: PhotoEntity)

    @Query("SELECT * FROM photos WHERE isSynced = 0")
    suspend fun getUnsyncedPhotos(): List<PhotoEntity>

    @Update
    suspend fun update(photo: PhotoEntity)
}
