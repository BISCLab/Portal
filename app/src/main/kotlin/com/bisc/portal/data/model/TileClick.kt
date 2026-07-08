package com.bisc.portal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tile_clicks")
data class TileClick(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tileId: Long,
    val url: String,
    val label: String,
    val timestamp: Long = System.currentTimeMillis()
)
