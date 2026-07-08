package com.bisc.portal.data.db

import androidx.room.TypeConverter
import com.bisc.portal.data.model.TileSize

class Converters {
    @TypeConverter fun fromTileSize(v: TileSize): String = v.name
    @TypeConverter fun toTileSize(v: String): TileSize = TileSize.valueOf(v)
}
