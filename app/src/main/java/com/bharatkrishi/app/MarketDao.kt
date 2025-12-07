package com.bharatkrishi.app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MarketDao {

    // If we get new data, replace the old data here
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(marketData: List<MarketData>)

    // Get all the saved data from the table
    @Query("SELECT * FROM market_data_table")
    suspend fun getAll(): List<MarketData>

    // Delete everything from the table (to clear out old data)
    @Query("DELETE FROM market_data_table")
    suspend fun deleteAll()
}
