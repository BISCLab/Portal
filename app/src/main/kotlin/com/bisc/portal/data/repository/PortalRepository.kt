package com.bisc.portal.data.repository

import com.bisc.portal.data.db.SectionDao
import com.bisc.portal.data.db.TileClickDao
import com.bisc.portal.data.db.TileDao
import com.bisc.portal.data.model.Section
import com.bisc.portal.data.model.SectionWithTiles
import com.bisc.portal.data.model.Tile
import com.bisc.portal.data.model.TileClick
import com.bisc.portal.data.model.effectiveColSpan
import com.bisc.portal.data.model.effectiveRowSpan
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PortalRepository @Inject constructor(
    private val sectionDao: SectionDao,
    private val tileDao: TileDao,
    private val tileClickDao: TileClickDao
) {
    val sectionsWithTiles: Flow<List<SectionWithTiles>> = sectionDao.observeAllWithTiles()
    val allClicks: Flow<List<TileClick>> = tileClickDao.allClicks()

    suspend fun recordClick(tileId: Long, url: String, label: String) =
        tileClickDao.insert(TileClick(tileId = tileId, url = url, label = label))

    suspend fun clearAllClicks() = tileClickDao.clearAll()

    suspend fun ensureDefaultSection() {
        if (sectionDao.getAll().isEmpty()) {
            repeat(3) { idx -> sectionDao.insert(Section(name = "", position = idx)) }
        }
    }

    suspend fun addSection(name: String) {
        val pos = sectionDao.getAll().size
        sectionDao.insert(Section(name = name, showName = name.isNotBlank(), position = pos))
    }

    suspend fun updateSection(section: Section) = sectionDao.update(section)
    suspend fun deleteSection(section: Section) = sectionDao.delete(section)

    /** Returns true if tile was placed, false if the grid is full (only when maxRows is finite). */
    suspend fun addTile(tile: Tile, internalCols: Int, maxRows: Int = Int.MAX_VALUE): Boolean {
        val existing = tileDao.getTilesForSection(tile.sectionId)
        val occupied = mutableSetOf<Pair<Int, Int>>()
        existing.forEach { t ->
            for (dr in 0 until t.effectiveRowSpan) {
                for (dc in 0 until t.effectiveColSpan) {
                    occupied.add(t.gridCol + dc to t.gridRow + dr)
                }
            }
        }
        val cs = tile.effectiveColSpan.coerceAtMost(internalCols)
        val rs = tile.effectiveRowSpan
        val placed = findFreeSlot(occupied, cs, rs, internalCols, maxRows) ?: return false
        val max = tileDao.maxPosition(tile.sectionId) ?: -1
        tileDao.insert(tile.copy(position = max + 1, gridCol = placed.first, gridRow = placed.second))
        return true
    }

    suspend fun updateTile(tile: Tile) = tileDao.update(tile)
    suspend fun deleteTile(tile: Tile) = tileDao.delete(tile)

    suspend fun setTileSpan(tileId: Long, colSpan: Int, rowSpan: Int) {
        tileDao.updateSpan(tileId, colSpan, rowSpan)
    }

    suspend fun setTilePosAndSpan(tileId: Long, col: Int, row: Int, colSpan: Int, rowSpan: Int) {
        tileDao.updatePosAndSpan(tileId, col, row, colSpan, rowSpan)
    }

    /**
     * Moves [tile] to (newCol, newRow) and displaces any overlapping tiles to the nearest free
     * slot, processing displaced tiles closest-first so the result is predictable.
     */
    suspend fun setTileGridPos(tile: Tile, newCol: Int, newRow: Int, internalCols: Int) {
        tileDao.updateGridPos(tile.id, newCol, newRow)

        val allTiles = tileDao.getTilesForSection(tile.sectionId)

        // Build the occupancy map starting with the moved tile's new cells
        val occupancy = mutableSetOf<Pair<Int, Int>>()
        for (dr in 0 until tile.effectiveRowSpan) {
            for (dc in 0 until tile.effectiveColSpan) {
                occupancy.add(newCol + dc to newRow + dr)
            }
        }

        // Sort remaining tiles by Manhattan distance from the drop point (closest displaced first)
        val others = allTiles
            .filter { it.id != tile.id }
            .sortedBy { t -> Math.abs(t.gridCol - newCol) + Math.abs(t.gridRow - newRow) }

        for (other in others) {
            val otherCells = buildList {
                for (dr in 0 until other.effectiveRowSpan) {
                    for (dc in 0 until other.effectiveColSpan) {
                        add(other.gridCol + dc to other.gridRow + dr)
                    }
                }
            }
            if (otherCells.any { it in occupancy }) {
                val newPos = findFreeSlot(occupancy, other.effectiveColSpan, other.effectiveRowSpan, internalCols)
                if (newPos != null) {
                    tileDao.updateGridPos(other.id, newPos.first, newPos.second)
                    for (dr in 0 until other.effectiveRowSpan) {
                        for (dc in 0 until other.effectiveColSpan) {
                            occupancy.add(newPos.first + dc to newPos.second + dr)
                        }
                    }
                } else {
                    // No room — leave the tile in place (overlapping); add to occupancy so further
                    // tiles don't try to land on it either.
                    occupancy.addAll(otherCells)
                }
            } else {
                occupancy.addAll(otherCells)
            }
        }
    }

    suspend fun getAllSections(): List<Section> = sectionDao.getAll()

    suspend fun getAllTiles(): List<Tile> =
        sectionDao.getAll().flatMap { tileDao.getTilesForSection(it.id) }

    suspend fun clearAndRestore(sections: List<Section>, tiles: List<Tile>) {
        tileDao.deleteAll()
        sectionDao.deleteAll()
        sections.forEach { sectionDao.insert(it) }
        tileDao.insertAll(tiles)
    }

    suspend fun moveTile(tileId: Long, targetSectionId: Long) {
        val existing = tileDao.getTilesForSection(targetSectionId)
        val max = existing.maxOfOrNull { it.position } ?: -1
        tileDao.moveToSection(tileId, targetSectionId, max + 1)
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    /** Scans row-by-row for the first slot large enough for (colSpan × rowSpan). */
    private fun findFreeSlot(
        occupancy: Set<Pair<Int, Int>>,
        colSpan: Int,
        rowSpan: Int,
        internalCols: Int,
        maxRows: Int = Int.MAX_VALUE
    ): Pair<Int, Int>? {
        val cs = colSpan.coerceAtMost(internalCols)
        val limit = minOf(maxRows, 400)
        for (r in 0 until limit) {
            for (c in 0..internalCols - cs) {
                val fits = (0 until rowSpan).all { dr ->
                    (r + dr) < limit && (0 until cs).all { dc -> (c + dc to r + dr) !in occupancy }
                }
                if (fits) return c to r
            }
        }
        return null
    }
}
