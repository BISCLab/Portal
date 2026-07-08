package com.bisc.portal.data.model

enum class TileSize(val colSpan: Int, val rowSpan: Int) {
    TINY(1, 1),
    SQUARE(2, 2),
    WIDE(4, 2),
    LARGE(4, 4)
}
