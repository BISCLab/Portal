package com.bisc.portal.data.db

import androidx.room.*
import com.bisc.portal.data.model.Section
import com.bisc.portal.data.model.SectionWithTiles
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionDao {
    @Transaction
    @Query("SELECT * FROM sections ORDER BY position ASC")
    fun observeAllWithTiles(): Flow<List<SectionWithTiles>>

    @Query("SELECT * FROM sections ORDER BY position ASC")
    suspend fun getAll(): List<Section>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(section: Section): Long

    @Update
    suspend fun update(section: Section)

    @Delete
    suspend fun delete(section: Section)

    @Query("UPDATE sections SET position = :position WHERE id = :id")
    suspend fun updatePosition(id: Long, position: Int)

    @Query("DELETE FROM sections")
    suspend fun deleteAll()
}
