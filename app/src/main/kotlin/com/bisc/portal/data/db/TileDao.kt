package com.bisc.portal.data.db

import androidx.room.*
import com.bisc.portal.data.model.Tile
import kotlinx.coroutines.flow.Flow

@Dao
interface TileDao {
    @Query("SELECT * FROM tiles WHERE sectionId = :sectionId ORDER BY position ASC")
    fun observeBySection(sectionId: Long): Flow<List<Tile>>

    @Query("SELECT * FROM tiles WHERE sectionId = :sectionId ORDER BY position ASC")
    suspend fun getTilesForSection(sectionId: Long): List<Tile>

    @Query("SELECT MAX(position) FROM tiles WHERE sectionId = :sectionId")
    suspend fun maxPosition(sectionId: Long): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tile: Tile): Long

    @Update
    suspend fun update(tile: Tile)

    @Delete
    suspend fun delete(tile: Tile)

    @Query("UPDATE tiles SET gridCol = :col, gridRow = :row WHERE id = :id")
    suspend fun updateGridPos(id: Long, col: Int, row: Int)

    @Query("UPDATE tiles SET colSpan = :colSpan, rowSpan = :rowSpan WHERE id = :id")
    suspend fun updateSpan(id: Long, colSpan: Int, rowSpan: Int)

    @Query("UPDATE tiles SET gridCol = :col, gridRow = :row, colSpan = :colSpan, rowSpan = :rowSpan WHERE id = :id")
    suspend fun updatePosAndSpan(id: Long, col: Int, row: Int, colSpan: Int, rowSpan: Int)

    @Query("UPDATE tiles SET sectionId = :sectionId, position = :position WHERE id = :id")
    suspend fun moveToSection(id: Long, sectionId: Long, position: Int)

    @Query("DELETE FROM tiles")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tiles: List<Tile>)
}
