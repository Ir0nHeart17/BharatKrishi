package com.bharatkrishi.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uri: String,
    val isSynced: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
