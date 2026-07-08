package com.bisc.portal.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.bisc.portal.data.model.Section
import com.bisc.portal.data.model.Tile
import com.bisc.portal.util.hexToColor
import com.bisc.portal.data.model.effectiveColSpan
import com.bisc.portal.data.model.effectiveRowSpan
import kotlin.math.roundToInt

private val HandleSize    = 40.dp   // visible + touch target (was 28dp — larger to avoid back-gesture conflicts)
private val HandleIconSize = 18.dp

// Sentinel ID for the section header tile drag/resize state maps
private const val HEADER_ID = Long.MIN_VALUE

@Composable
fun TileGrid(
    tiles: List<Tile>,
    columns: Int,
    gap: Dp,
    editMode: Boolean,
    onEnterEditMode: () -> Unit,
    maxHeightDp: Dp? = null,
    unitHDp: Dp? = null,
    globalAutoInvertIcons: Boolean = false,
    sectionHeader: Section? = null,
    onUpdateSectionHeader: (Section) -> Unit = {},
    modifier: Modifier = Modifier,
    onTileClick: (Tile) -> Unit,
    onTileLongClick: (Tile) -> Unit,
    onTileMove: (tileId: Long, newCol: Int, newRow: Int) -> Unit,
    onTileResize: (tile: Tile, newCols: Int, newRows: Int) -> Unit,
    onTileMoveAndResize: (tile: Tile, newCol: Int, newRow: Int, newCols: Int, newRows: Int) -> Unit = { _, _, _, _, _ -> }
) {
    val density = LocalDensity.current

    val dragOffsets         = remember { mutableStateMapOf<Long, Offset>() }
    val resizeWidthOffsets  = remember { mutableStateMapOf<Long, Float>() }
    val resizeHeightOffsets = remember { mutableStateMapOf<Long, Float>() }
    val resizeLeftOffsets   = remember { mutableStateMapOf<Long, Float>() }
    val resizeTopOffsets    = remember { mutableStateMapOf<Long, Float>() }

    val primaryColor = MaterialTheme.colorScheme.primary

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val totalWidthPx  = constraints.maxWidth.toFloat()
        val gapPx         = with(density) { gap.toPx() }
        val internalCols  = (columns * 2).coerceAtLeast(2)
        val unitPx        = ((totalWidthPx - gapPx * (internalCols - 1)) / internalCols).coerceAtLeast(1f)
        val unitHPx       = unitHDp?.let { with(density) { it.toPx() } } ?: unitPx
        val dragThreshPx  = with(density) { 14.dp.toPx() }
        val handlePx      = with(density) { HandleSize.toPx() }
        val handleRadius  = handlePx / 2f

        val staticH  = tiles.maxOfOrNull { t ->
            (t.gridRow + t.effectiveRowSpan) * (unitHPx + gapPx) - gapPx
        } ?: 0f
        val rawGridH = with(density) { (staticH + unitHPx * 3 + gapPx * 2).toDp() }
        val gridH    = maxHeightDp ?: rawGridH
        val gridHPx  = with(density) { gridH.toPx() }
        Box(modifier = Modifier.fillMaxWidth().height(gridH)
            .then(if (editMode) Modifier.systemGestureExclusion() else Modifier)
        ) {

            tiles.forEach { tile ->
                val originX    = tile.gridCol * (unitPx + gapPx)
                val originY    = tile.gridRow * (unitHPx + gapPx)
                val tileW      = unitPx  * tile.effectiveColSpan + gapPx * (tile.effectiveColSpan - 1)
                val tileH      = unitHPx * tile.effectiveRowSpan + gapPx * (tile.effectiveRowSpan - 1)
                val dragOff    = dragOffsets[tile.id] ?: Offset.Zero
                val cumResizeW = resizeWidthOffsets[tile.id]  ?: 0f
                val cumResizeH = resizeHeightOffsets[tile.id] ?: 0f
                val cumResizeL = resizeLeftOffsets[tile.id]   ?: 0f
                val cumResizeT = resizeTopOffsets[tile.id]    ?: 0f

                val displayW = when {
                    cumResizeL != 0f -> (tileW - cumResizeL).coerceAtLeast(unitPx)
                    cumResizeW != 0f -> (tileW + cumResizeW).coerceAtLeast(unitPx)
                    else             -> tileW
                }
                val displayH = when {
                    cumResizeT != 0f -> (tileH - cumResizeT).coerceAtLeast(unitHPx)
                    cumResizeH != 0f -> (tileH + cumResizeH).coerceAtLeast(unitHPx)
                    else             -> tileH
                }

                val tileX = when {
                    cumResizeL != 0f -> (originX + cumResizeL).coerceAtLeast(0f)
                    else             -> originX + dragOff.x
                }
                val tileY = when {
                    cumResizeT != 0f -> (originY + cumResizeT).coerceAtLeast(0f)
                    else             -> originY + dragOff.y
                }

                TileCard(
                    tile = tile,
                    globalAutoInvertIcons = globalAutoInvertIcons,
                    modifier = Modifier
                        .size(
                            width  = with(density) { displayW.toDp() },
                            height = with(density) { displayH.toDp() }
                        )
                        .offset { IntOffset(tileX.roundToInt(), tileY.roundToInt()) }
                        .zIndex(if (dragOff != Offset.Zero) 1f else 0f)
                        .then(
                            if (editMode) Modifier.border(1.5.dp, primaryColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            else Modifier
                        )
                        .pointerInput(tile.id) {
                            detectTapGestures(onTap = { onTileClick(tile) })
                        }
                        .pointerInput(tile.id, originX, originY, unitPx, unitHPx, gapPx, internalCols, tile.effectiveColSpan, tile.effectiveRowSpan) {
                            var cum = Offset.Zero
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    onEnterEditMode()
                                    cum = Offset.Zero
                                    dragOffsets[tile.id] = Offset.Zero
                                },
                                onDrag = { change, delta ->
                                    change.consume()
                                    cum += delta
                                    dragOffsets[tile.id] = cum
                                },
                                onDragEnd = {
                                    if (cum.getDistance() > dragThreshPx) {
                                        val newX = originX + cum.x
                                        val newY = originY + cum.y
                                        val newCol = (newX / (unitPx  + gapPx))
                                            .roundToInt()
                                            .coerceIn(0, internalCols - tile.effectiveColSpan)
                                        val newRow = (newY / (unitHPx + gapPx))
                                            .roundToInt()
                                            .coerceAtLeast(0)
                                        onTileMove(tile.id, newCol, newRow)
                                    } else {
                                        onTileLongClick(tile)
                                    }
                                    dragOffsets.remove(tile.id)
                                },
                                onDragCancel = { dragOffsets.remove(tile.id) }
                            )
                        }
                )

                if (editMode) {
                    ResizeHandle(Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        (tileX + displayW - handleRadius).roundToInt(),
                        (tileY + displayH / 2f - handleRadius).roundToInt(), primaryColor
                    ) { _ ->
                        Modifier.pointerInput(tile.id, tileW, unitPx, gapPx, internalCols, tile.gridCol) {
                            var cumW = 0f
                            detectDragGestures(
                                onDragStart = { cumW = 0f; resizeWidthOffsets[tile.id] = 0f },
                                onDrag = { c, d -> c.consume(); cumW += d.x; resizeWidthOffsets[tile.id] = cumW },
                                onDragEnd = {
                                    val nc = ((tileW + cumW) / (unitPx + gapPx))
                                        .roundToInt().coerceIn(1, internalCols - tile.gridCol)
                                    onTileResize(tile, nc, tile.effectiveRowSpan)
                                    resizeWidthOffsets.remove(tile.id)
                                },
                                onDragCancel = { resizeWidthOffsets.remove(tile.id) }
                            )
                        }
                    }
                    ResizeHandle(Icons.Outlined.KeyboardArrowDown,
                        (tileX + displayW / 2f - handleRadius).roundToInt(),
                        (tileY + displayH - handleRadius).roundToInt(), primaryColor
                    ) { _ ->
                        Modifier.pointerInput(tile.id, tileH, unitHPx, gapPx) {
                            var cumH = 0f
                            detectDragGestures(
                                onDragStart = { cumH = 0f; resizeHeightOffsets[tile.id] = 0f },
                                onDrag = { c, d -> c.consume(); cumH += d.y; resizeHeightOffsets[tile.id] = cumH },
                                onDragEnd = {
                                    val nr = ((tileH + cumH) / (unitHPx + gapPx))
                                        .roundToInt().coerceAtLeast(1)
                                    onTileResize(tile, tile.effectiveColSpan, nr)
                                    resizeHeightOffsets.remove(tile.id)
                                },
                                onDragCancel = { resizeHeightOffsets.remove(tile.id) }
                            )
                        }
                    }
                    ResizeHandle(Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                        (tileX - handleRadius).roundToInt(),
                        (tileY + displayH / 2f - handleRadius).roundToInt(), primaryColor
                    ) { _ ->
                        Modifier.pointerInput(tile.id, originX, tileW, unitPx, gapPx, tile.gridCol, tile.effectiveColSpan) {
                            var cumL = 0f
                            detectDragGestures(
                                onDragStart = { cumL = 0f; resizeLeftOffsets[tile.id] = 0f },
                                onDrag = { c, d -> c.consume(); cumL += d.x; resizeLeftOffsets[tile.id] = cumL },
                                onDragEnd = {
                                    val newLeftPx = (originX + cumL).coerceAtLeast(0f)
                                    val nc = (newLeftPx / (unitPx + gapPx)).roundToInt()
                                        .coerceIn(0, tile.gridCol + tile.effectiveColSpan - 1)
                                    onTileMoveAndResize(tile, nc, tile.gridRow,
                                        (tile.gridCol + tile.effectiveColSpan - nc).coerceAtLeast(1),
                                        tile.effectiveRowSpan)
                                    resizeLeftOffsets.remove(tile.id)
                                },
                                onDragCancel = { resizeLeftOffsets.remove(tile.id) }
                            )
                        }
                    }
                    ResizeHandle(Icons.Outlined.KeyboardArrowUp,
                        (tileX + displayW / 2f - handleRadius).roundToInt(),
                        (tileY - handleRadius).roundToInt(), primaryColor
                    ) { _ ->
                        Modifier.pointerInput(tile.id, originY, tileH, unitHPx, gapPx, tile.gridRow, tile.effectiveRowSpan) {
                            var cumT = 0f
                            detectDragGestures(
                                onDragStart = { cumT = 0f; resizeTopOffsets[tile.id] = 0f },
                                onDrag = { c, d -> c.consume(); cumT += d.y; resizeTopOffsets[tile.id] = cumT },
                                onDragEnd = {
                                    val newTopPx = (originY + cumT).coerceAtLeast(0f)
                                    val nr = (newTopPx / (unitHPx + gapPx)).roundToInt()
                                        .coerceIn(0, tile.gridRow + tile.effectiveRowSpan - 1)
                                    onTileMoveAndResize(tile, tile.gridCol, nr,
                                        tile.effectiveColSpan,
                                        (tile.gridRow + tile.effectiveRowSpan - nr).coerceAtLeast(1))
                                    resizeTopOffsets.remove(tile.id)
                                },
                                onDragCancel = { resizeTopOffsets.remove(tile.id) }
                            )
                        }
                    }
                }
            }

            sectionHeader?.let { section ->
                val hOriginX = section.headerGridCol * (unitPx + gapPx)
                val hOriginY = section.headerGridRow * (unitHPx + gapPx)
                val hTileW   = unitPx  * section.headerColSpan + gapPx * (section.headerColSpan - 1)
                val hTileH   = unitHPx * section.headerRowSpan + gapPx * (section.headerRowSpan - 1)

                val hDragOff   = dragOffsets[HEADER_ID]         ?: Offset.Zero
                val hCumResizeW = resizeWidthOffsets[HEADER_ID]  ?: 0f
                val hCumResizeH = resizeHeightOffsets[HEADER_ID] ?: 0f
                val hCumResizeL = resizeLeftOffsets[HEADER_ID]   ?: 0f
                val hCumResizeT = resizeTopOffsets[HEADER_ID]    ?: 0f

                val hDisplayW = when {
                    hCumResizeL != 0f -> (hTileW - hCumResizeL).coerceAtLeast(unitPx)
                    hCumResizeW != 0f -> (hTileW + hCumResizeW).coerceAtLeast(unitPx)
                    else              -> hTileW
                }
                val hDisplayH = when {
                    hCumResizeT != 0f -> (hTileH - hCumResizeT).coerceAtLeast(unitHPx)
                    hCumResizeH != 0f -> (hTileH + hCumResizeH).coerceAtLeast(unitHPx)
                    else              -> hTileH
                }
                val hTileX = when {
                    hCumResizeL != 0f -> (hOriginX + hCumResizeL).coerceAtLeast(0f)
                    else              -> hOriginX + hDragOff.x
                }
                val hTileY = when {
                    hCumResizeT != 0f -> (hOriginY + hCumResizeT).coerceAtLeast(0f)
                    else              -> hOriginY + hDragOff.y
                }

                Box(
                    modifier = Modifier
                        .size(
                            width  = with(density) { hDisplayW.toDp() },
                            height = with(density) { hDisplayH.toDp() }
                        )
                        .offset { IntOffset(hTileX.roundToInt(), hTileY.roundToInt()) }
                        .zIndex(if (hDragOff != Offset.Zero) 2f else 0.5f)
                        .clip(RoundedCornerShape(section.headerRadius.dp))
                        .background(
                            section.headerBgColor.hexToColor() ?: Color(0xFF000B29)
                        )
                        .then(
                            if (editMode) Modifier.border(1.5.dp, primaryColor.copy(alpha = 0.6f), RoundedCornerShape(section.headerRadius.dp))
                            else Modifier
                        )
                        .pointerInput(HEADER_ID, hOriginX, hOriginY, unitPx, unitHPx, gapPx) {
                            if (!editMode) return@pointerInput
                            var cum = Offset.Zero
                            detectDragGesturesAfterLongPress(
                                onDragStart = { cum = Offset.Zero; dragOffsets[HEADER_ID] = Offset.Zero },
                                onDrag = { c, d -> c.consume(); cum += d; dragOffsets[HEADER_ID] = cum },
                                onDragEnd = {
                                    val newX = hOriginX + cum.x
                                    val newY = hOriginY + cum.y
                                    val nc = (newX / (unitPx  + gapPx)).roundToInt()
                                        .coerceIn(0, internalCols - section.headerColSpan)
                                    val nr = (newY / (unitHPx + gapPx)).roundToInt().coerceAtLeast(0)
                                    onUpdateSectionHeader(section.copy(headerGridCol = nc, headerGridRow = nr))
                                    dragOffsets.remove(HEADER_ID)
                                },
                                onDragCancel = { dragOffsets.remove(HEADER_ID) }
                            )
                        }
                ) {
                    SectionHeaderContent(section = section, unitPx = unitPx)
                }

                if (editMode) {
                    ResizeHandle(Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        (hTileX + hDisplayW - handleRadius).roundToInt(),
                        (hTileY + hDisplayH / 2f - handleRadius).roundToInt(), primaryColor
                    ) { _ ->
                        Modifier.pointerInput(HEADER_ID, hTileW, unitPx, gapPx, internalCols, section.headerGridCol) {
                            var cumW = 0f
                            detectDragGestures(
                                onDragStart = { cumW = 0f; resizeWidthOffsets[HEADER_ID] = 0f },
                                onDrag = { c, d -> c.consume(); cumW += d.x; resizeWidthOffsets[HEADER_ID] = cumW },
                                onDragEnd = {
                                    val nc = ((hTileW + cumW) / (unitPx + gapPx))
                                        .roundToInt().coerceIn(1, internalCols - section.headerGridCol)
                                    onUpdateSectionHeader(section.copy(headerColSpan = nc))
                                    resizeWidthOffsets.remove(HEADER_ID)
                                },
                                onDragCancel = { resizeWidthOffsets.remove(HEADER_ID) }
                            )
                        }
                    }
                    ResizeHandle(Icons.Outlined.KeyboardArrowDown,
                        (hTileX + hDisplayW / 2f - handleRadius).roundToInt(),
                        (hTileY + hDisplayH - handleRadius).roundToInt(), primaryColor
                    ) { _ ->
                        Modifier.pointerInput(HEADER_ID, hTileH, unitHPx, gapPx) {
                            var cumH = 0f
                            detectDragGestures(
                                onDragStart = { cumH = 0f; resizeHeightOffsets[HEADER_ID] = 0f },
                                onDrag = { c, d -> c.consume(); cumH += d.y; resizeHeightOffsets[HEADER_ID] = cumH },
                                onDragEnd = {
                                    val nr = ((hTileH + cumH) / (unitHPx + gapPx))
                                        .roundToInt().coerceAtLeast(1)
                                    onUpdateSectionHeader(section.copy(headerRowSpan = nr))
                                    resizeHeightOffsets.remove(HEADER_ID)
                                },
                                onDragCancel = { resizeHeightOffsets.remove(HEADER_ID) }
                            )
                        }
                    }
                    ResizeHandle(Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                        (hTileX - handleRadius).roundToInt(),
                        (hTileY + hDisplayH / 2f - handleRadius).roundToInt(), primaryColor
                    ) { _ ->
                        Modifier.pointerInput(HEADER_ID, hOriginX, hTileW, unitPx, gapPx, section.headerGridCol, section.headerColSpan) {
                            var cumL = 0f
                            detectDragGestures(
                                onDragStart = { cumL = 0f; resizeLeftOffsets[HEADER_ID] = 0f },
                                onDrag = { c, d -> c.consume(); cumL += d.x; resizeLeftOffsets[HEADER_ID] = cumL },
                                onDragEnd = {
                                    val newLeftPx = (hOriginX + cumL).coerceAtLeast(0f)
                                    val nc = (newLeftPx / (unitPx + gapPx)).roundToInt()
                                        .coerceIn(0, section.headerGridCol + section.headerColSpan - 1)
                                    onUpdateSectionHeader(section.copy(
                                        headerGridCol = nc,
                                        headerColSpan = (section.headerGridCol + section.headerColSpan - nc).coerceAtLeast(1)
                                    ))
                                    resizeLeftOffsets.remove(HEADER_ID)
                                },
                                onDragCancel = { resizeLeftOffsets.remove(HEADER_ID) }
                            )
                        }
                    }
                    ResizeHandle(Icons.Outlined.KeyboardArrowUp,
                        (hTileX + hDisplayW / 2f - handleRadius).roundToInt(),
                        (hTileY - handleRadius).roundToInt(), primaryColor
                    ) { _ ->
                        Modifier.pointerInput(HEADER_ID, hOriginY, hTileH, unitHPx, gapPx, section.headerGridRow, section.headerRowSpan) {
                            var cumT = 0f
                            detectDragGestures(
                                onDragStart = { cumT = 0f; resizeTopOffsets[HEADER_ID] = 0f },
                                onDrag = { c, d -> c.consume(); cumT += d.y; resizeTopOffsets[HEADER_ID] = cumT },
                                onDragEnd = {
                                    val newTopPx = (hOriginY + cumT).coerceAtLeast(0f)
                                    val nr = (newTopPx / (unitHPx + gapPx)).roundToInt()
                                        .coerceIn(0, section.headerGridRow + section.headerRowSpan - 1)
                                    onUpdateSectionHeader(section.copy(
                                        headerGridRow = nr,
                                        headerRowSpan = (section.headerGridRow + section.headerRowSpan - nr).coerceAtLeast(1)
                                    ))
                                    resizeTopOffsets.remove(HEADER_ID)
                                },
                                onDragCancel = { resizeTopOffsets.remove(HEADER_ID) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResizeHandle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    offsetX: Int,
    offsetY: Int,
    color: Color,
    buildGesture: @Composable (Modifier) -> Modifier
) {
    val gestureModifier = buildGesture(Modifier)
    Box(
        modifier = Modifier
            .size(HandleSize)
            .offset { IntOffset(offsetX, offsetY) }
            .zIndex(4f)
            .clip(CircleShape)
            .background(color)
            .then(gestureModifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(HandleIconSize)
        )
    }
}
