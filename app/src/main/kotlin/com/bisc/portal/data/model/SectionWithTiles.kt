package com.bisc.portal.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class SectionWithTiles(
    @Embedded val section: Section,
    @Relation(
        parentColumn = "id",
        entityColumn = "sectionId"
    )
    val tiles: List<Tile>
) {
    val sortedTiles: List<Tile> get() = tiles.sortedBy { it.position }
}
