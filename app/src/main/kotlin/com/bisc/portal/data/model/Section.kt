package com.bisc.portal.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sections")
data class Section(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "",
    val showName: Boolean = false,
    val position: Int = 0,
    val headerBgColor: String = "000B29",
    val headerIcon: String = "",
    val headerHeight: Int = 1,     // kept for compat, no longer used in UI
    val sectionColumns: Int = 0,   // header mode: 0=hidden, 1=icon, 2=icon+text, 3=text
    val headerRadius: Int = 0,     // dp, 0 = sharp corners
    // header tile grid position + span (in internal grid units)
    val headerGridCol: Int = 0,
    val headerGridRow: Int = 0,
    val headerColSpan: Int = 2,    // default 1 real col wide
    val headerRowSpan: Int = 10,   // default 5 real rows tall
    val gridPreset: Int = 0,       // 0 = 10×18 (default), 1 = 12×24 (large)
)
