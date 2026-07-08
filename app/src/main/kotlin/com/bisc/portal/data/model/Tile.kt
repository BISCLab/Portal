package com.bisc.portal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tiles")
data class Tile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sectionId: Long,
    val label: String = "",
    val showLabel: Boolean = false,
    val url: String,
    val iconAsset: String = "",
    val iconUri: String = "",
    val size: TileSize = TileSize.SQUARE,
    val position: Int = 0,
    val gridCol: Int = 0,
    val gridRow: Int = 0,
    val colSpan: Int = 2,
    val rowSpan: Int = 2,
    // Icon appearance (per-tile)
    val iconShape: String = "square",   // "square" | "round"
    val iconScale: String = "crop",     // "crop" | "fit" | "inside"
    val iconBgColor: String = "",       // hex without #, "" = none
    val iconBgEnabled: Boolean = false,
    val invertIcon: Boolean = false,
    val autoInvertIcon: Boolean = false,
    val isTextTile: Boolean = false,
    val labelColor: String = "FFFFFF",  // hex without #, default white
    val labelAlign: Int = 0,            // 0=Start, 1=Center, 2=End
    val labelFontSize: Int = 0,         // 0=auto, else sp value
    val isIconTextTile: Boolean = false,
    val iconTextPosition: Int = 0,      // 0=icon left, 1=icon right, 2=icon top, 3=icon bottom
    val labelBold: Boolean = false,
    val labelItalic: Boolean = false,
    val iconZoom: Float = 1f,
)

val Tile.effectiveColSpan: Int get() = if (colSpan > 0) colSpan else size.colSpan
val Tile.effectiveRowSpan: Int get() = if (rowSpan > 0) rowSpan else size.rowSpan
