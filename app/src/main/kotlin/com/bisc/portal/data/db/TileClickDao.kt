package com.bisc.portal.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bisc.portal.data.model.TileClick
import kotlinx.coroutines.flow.Flow

@Dao
interface TileClickDao {
    @Insert
    suspend fun insert(click: TileClick)

    @Query("SELECT * FROM tile_clicks ORDER BY timestamp DESC")
    fun allClicks(): Flow<List<TileClick>>

    @Query("DELETE FROM tile_clicks")
    suspend fun clearAll()
}
