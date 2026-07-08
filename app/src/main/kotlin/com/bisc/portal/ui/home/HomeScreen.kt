package com.bisc.portal.ui.home

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bisc.portal.data.model.Section
import com.bisc.portal.data.model.Tile
import com.bisc.portal.ui.sheet.AddEditTileSheet
import com.bisc.portal.util.normalizeUrl
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


@Composable
fun HomeScreen(
    vm: HomeViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    val context       = LocalContext.current
    val density       = LocalDensity.current
    val sections      by vm.sectionsWithTiles.collectAsState()
    val columns       by vm.columns.collectAsState()
    val gapDp         by vm.tileGapDp.collectAsState()
    val outerPadDp    by vm.outerPaddingDp.collectAsState()
    val separatorDp   by vm.bottomSeparatorDp.collectAsState()
    val editLock        by vm.editLock.collectAsState()
    val infiniteScroll  by vm.infiniteScroll.collectAsState()
    val autoCollapse      by vm.autoCollapse.collectAsState()
    val barShowGesture    by vm.barShowGesture.collectAsState()
    val barMode           by vm.barMode.collectAsState()
    val rows              by vm.rows.collectAsState()
    val btnColorBack      by vm.btnColorBack.collectAsState()
    val btnColorSettings by vm.btnColorSettings.collectAsState()
    val btnColorAdd     by vm.btnColorAdd.collectAsState()
    val btnColorInfo    by vm.btnColorInfo.collectAsState()
    val btnColorForward  by vm.btnColorForward.collectAsState()
    val horizontalWrap    by vm.horizontalWrap.collectAsState()
    val topSeparatorDp    by vm.topSeparatorDp.collectAsState()
    val leftSeparatorDp   by vm.leftSeparatorDp.collectAsState()
    val rightSeparatorDp  by vm.rightSeparatorDp.collectAsState()
    val autoInvertIcons   by vm.autoInvertIcons.collectAsState()
    val browserPackage    by vm.browserPackage.collectAsState()
    val arrowDimAtEnds    by vm.arrowDimAtEnds.collectAsState()
    val btnIconBack       by vm.btnIconBack.collectAsState()
    val btnIconSettings   by vm.btnIconSettings.collectAsState()
    val btnIconAdd        by vm.btnIconAdd.collectAsState()
    val btnIconInfo       by vm.btnIconInfo.collectAsState()
    val btnIconForward    by vm.btnIconForward.collectAsState()

    var addTargetSectionId   by remember { mutableStateOf<Long?>(null) }
    var editingTile          by remember { mutableStateOf<Tile?>(null) }
    var editMode             by remember { mutableStateOf(false) }
    var barCollapsed         by remember { mutableStateOf(false) }
    var hiddenBarVisible     by remember { mutableStateOf(false) }

    val barVisible = when (barMode) {
        "always" -> true
        "hidden" -> hiddenBarVisible
        else     -> !barCollapsed
    }
    val isWrap = horizontalWrap && sections.size > 1

    val snackbarHost  = remember { SnackbarHostState() }
    val scope         = rememberCoroutineScope()

    val pagerState = rememberPagerState(pageCount = {
        if (isWrap && sections.size > 1) Int.MAX_VALUE / 2 else sections.size.coerceAtLeast(1)
    })

    // Keep pager centered in virtual space so wrap swipes work in both directions.
    // Also snaps back to real index when wrap is disabled.
    LaunchedEffect(isWrap, sections.size) {
        if (sections.isEmpty()) return@LaunchedEffect
        if (isWrap && sections.size > 1) {
            if (pagerState.currentPage < sections.size) {
                pagerState.scrollToPage(Int.MAX_VALUE / 4 + pagerState.currentPage)
            }
        } else {
            if (pagerState.currentPage >= sections.size) {
                pagerState.scrollToPage(pagerState.currentPage % sections.size.coerceAtLeast(1))
            }
        }
    }

    LaunchedEffect(barCollapsed, autoCollapse, barMode) {
        if (!barCollapsed && autoCollapse && barMode == "collapsible") {
            delay(4000)
            barCollapsed = true
        }
    }

    LaunchedEffect(hiddenBarVisible, barMode) {
        if (hiddenBarVisible && barMode == "hidden") {
            delay(4000)
            hiddenBarVisible = false
        }
    }

    LaunchedEffect(vm) {
        vm.events.collect { event ->
            when (event) {
                is HomeEvent.ScreenFull ->
                    snackbarHost.showSnackbar("Diese Seite ist voll. Bitte eine andere Seite wählen oder ein Icon löschen.")
            }
        }
    }

    val realPageIdx = if (isWrap && sections.size > 1)
        pagerState.currentPage % sections.size else pagerState.currentPage
    val currentSectionId = sections.getOrNull(realPageIdx)?.section?.id
        ?: sections.firstOrNull()?.section?.id ?: 0L

    BackHandler(enabled = editMode) { editMode = false }

    // Disable system edge swipe gestures (back gesture) during edit/drag mode
    val view = LocalView.current
    LaunchedEffect(editMode) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            view.post {
                view.systemGestureExclusionRects = if (editMode)
                    listOf(android.graphics.Rect(0, 0, view.width, view.height))
                else
                    emptyList()
            }
        }
    }

    val openTile: (Tile) -> Unit = { tile ->
        vm.recordClick(tile)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(normalizeUrl(tile.url)))
        if (browserPackage.isNotEmpty()) {
            intent.setPackage(browserPackage)
            try { context.startActivity(intent) }
            catch (_: Exception) { intent.setPackage(null); context.startActivity(intent) }
        } else {
            context.startActivity(intent)
        }
    }

    // Graph-paper grid colours (need MaterialTheme, computed here)
    val graphLineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHost) { data ->
                Snackbar(snackbarData = data, modifier = Modifier.padding(16.dp))
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { scaffoldPad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPad)
                .then(
                    if (barMode == "hidden" && !hiddenBarVisible)
                        Modifier.pointerInput(barMode, hiddenBarVisible) {
                            coroutineScope {
                                launch {
                                    detectTapGestures(onDoubleTap = { hiddenBarVisible = true })
                                }
                                launch {
                                    var acc = 0f
                                    detectVerticalDragGestures(
                                        onDragEnd    = { acc = 0f },
                                        onDragCancel = { acc = 0f }
                                    ) { change, delta ->
                                        change.consume()
                                        acc += delta
                                        if (acc < -50f) { hiddenBarVisible = true; acc = 0f }
                                    }
                                }
                            }
                        }
                    else Modifier
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                BoxWithConstraints(modifier = Modifier.weight(1f)) {
                    val availableHeightPx = constraints.maxHeight.toFloat()
                    val gapPx             = with(density) { gapDp.dp.toPx() }
                    val outerPadPx        = with(density) { outerPadDp.dp.toPx() }
                    val leftSepPx         = with(density) { leftSeparatorDp.dp.toPx() }
                    val rightSepPx        = with(density) { rightSeparatorDp.dp.toPx() }
                    val topSepPx          = with(density) { topSeparatorDp.dp.toPx() }
                    val totalWidthPx      = constraints.maxWidth.toFloat()
                    val effectiveWidthPx  = (totalWidthPx - outerPadPx - leftSepPx - outerPadPx - rightSepPx).coerceAtLeast(1f)
                    val leftPadPx         = outerPadPx + leftSepPx
                    val effectiveW        = effectiveWidthPx
                    // Compute unitHPx to exactly fill available height — grid rows snap to bottom bar
                    val gridAreaH         = (availableHeightPx - topSepPx).coerceAtLeast(1f)

                    // Canvas grid overlay uses the current page's preset so lines match tile layout
                    val curRealIdx        = if (isWrap && sections.size > 1) pagerState.currentPage % sections.size else pagerState.currentPage
                    val currentSection    = sections.getOrNull(curRealIdx)?.section
                    val internalCols      = if (currentSection?.gridPreset == 1) 12 else (columns * 2).coerceAtLeast(2)
                    val internalRows      = if (currentSection?.gridPreset == 1) 24 else (rows * 2).coerceAtLeast(2)
                    val unitPx            = ((effectiveWidthPx - gapPx * (internalCols - 1)) / internalCols).coerceAtLeast(1f)
                    val unitHPx           = if (infiniteScroll) unitPx
                        else ((gridAreaH - gapPx * (internalRows - 1)) / internalRows).coerceAtLeast(1f)

                    val pageContent: @Composable (sectionIndex: Int) -> Unit = { page ->
                        val realIdx = if (isWrap && sections.size > 1) page % sections.size else page
                        sections.getOrNull(realIdx)?.let { swt ->
                            val pageCols    = if (swt.section.gridPreset == 1) 6 else columns
                            val pageIRows   = if (swt.section.gridPreset == 1) 24 else (rows * 2).coerceAtLeast(2)
                            val pageIHPx    = if (infiniteScroll) unitPx
                                else ((gridAreaH - gapPx * (pageIRows - 1)) / pageIRows).coerceAtLeast(1f)
                            val pageMaxH: Dp? = if (infiniteScroll) null else with(density) { gridAreaH.toDp() }
                            val pageUnitHDp: Dp? = if (infiniteScroll) null else with(density) { pageIHPx.toDp() }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                userScrollEnabled = infiniteScroll,
                                contentPadding = PaddingValues(
                                    start = outerPadDp.dp + leftSeparatorDp.dp,
                                    end   = outerPadDp.dp + rightSeparatorDp.dp,
                                    top   = topSeparatorDp.dp
                                )
                            ) {
                                item {
                                    TileGrid(
                                        tiles = swt.sortedTiles,
                                        columns = pageCols,
                                        gap = gapDp.dp,
                                        editMode = editMode,
                                        onEnterEditMode = { if (!editLock) editMode = true },
                                        maxHeightDp = pageMaxH,
                                        unitHDp = pageUnitHDp,
                                        globalAutoInvertIcons = autoInvertIcons,
                                        sectionHeader = null,
                                        onUpdateSectionHeader = { vm.updateSection(it) },
                                        onTileClick = openTile,
                                        onTileLongClick = { editingTile = it },
                                        onTileMove = { id, col, row -> vm.setTileGridPos(id, col, row, pageCols * 2) },
                                        onTileResize = { tile, newCols, newRows -> vm.setTileSpan(tile.id, newCols, newRows) },
                                        onTileMoveAndResize = { tile, col, row, cols, rows -> vm.setTilePosAndSpan(tile.id, col, row, cols, rows) }
                                    )
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        // Grid background (edit mode) — shares same coordinate space as content
                        if (editMode) {
                            Canvas(Modifier.fillMaxSize()) {
                                val strokeW = 0.7.dp.toPx()
                                for (c in 0..internalCols) {
                                    val x = leftPadPx + if (c < internalCols) c * (unitPx + gapPx) else effectiveW
                                    drawLine(graphLineColor, Offset(x, 0f), Offset(x, size.height), strokeW)
                                }
                                for (r in 0..internalRows) {
                                    val y = topSepPx + r * (unitHPx + gapPx)
                                    drawLine(graphLineColor, Offset(0f, y), Offset(size.width, y), strokeW)
                                }
                            }
                        }

                        if (sections.size <= 1) {
                            pageContent(0)
                        } else {
                            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                                pageContent(page)
                            }
                        }
                    }
                }

                // Separator strip (no background — grid shows through in edit mode)
                if (separatorDp > 0f && barMode != "hidden") {
                    Spacer(modifier = Modifier.fillMaxWidth().height(separatorDp.dp))
                }

                AnimatedVisibility(
                    visible = barVisible,
                    enter = slideInVertically { it },
                    exit  = slideOutVertically { it }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (barMode == "collapsible") Modifier.pointerInput(Unit) {
                                    var acc = 0f
                                    detectVerticalDragGestures(
                                        onDragEnd    = { acc = 0f },
                                        onDragCancel = { acc = 0f }
                                    ) { change, delta ->
                                        change.consume()
                                        acc += delta
                                        if (acc > 60f) { barCollapsed = true; acc = 0f }
                                    }
                                } else Modifier
                            )
                    ) {
                        PortalBottomBar(
                            canGoBack    = !arrowDimAtEnds || isWrap || realPageIdx > 0,
                            canGoForward = !arrowDimAtEnds || isWrap || realPageIdx < sections.size - 1,
                            onBack = { scope.launch {
                                if (isWrap && sections.size > 1) {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                } else {
                                    pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceAtLeast(0))
                                }
                            }},
                            onForward = { scope.launch {
                                if (isWrap && sections.size > 1) {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                } else {
                                    pagerState.animateScrollToPage((pagerState.currentPage + 1).coerceAtMost(sections.size - 1))
                                }
                            }},
                            editMode      = editMode,
                            onAdd         = { addTargetSectionId = currentSectionId },
                            onDoneEditing = { editMode = false },
                            onSettings    = onNavigateToSettings,
                            onInfo        = onNavigateToAbout,
                            colorBack     = btnColorBack,
                            colorSettings = btnColorSettings,
                            colorAdd      = btnColorAdd,
                            colorInfo     = btnColorInfo,
                            colorForward  = btnColorForward,
                            iconBack      = btnIconBack,
                            iconSettings  = btnIconSettings,
                            iconAdd       = btnIconAdd,
                            iconInfo      = btnIconInfo,
                            iconForward   = btnIconForward,
                        )
                    }
                }

                if (barMode == "collapsible") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (barCollapsed) 48.dp else 0.dp)
                            .pointerInput(barCollapsed, barShowGesture) {
                                if (!barCollapsed) return@pointerInput
                                when (barShowGesture) {
                                    "double_tap" -> detectTapGestures(onDoubleTap = { barCollapsed = false })
                                    "tap"        -> detectTapGestures(onTap = { barCollapsed = false })
                                    else -> {
                                        var acc = 0f
                                        detectVerticalDragGestures(
                                            onDragEnd    = { acc = 0f },
                                            onDragCancel = { acc = 0f }
                                        ) { change, delta ->
                                            change.consume()
                                            acc += delta
                                            if (acc < -30f) { barCollapsed = false; acc = 0f }
                                        }
                                    }
                                }
                            }
                    )
                }

                // Emergency settings access — in hidden mode when editing
                if (barMode == "hidden" && editMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f))
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.TextButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Outlined.Settings, contentDescription = null, modifier = Modifier.size(18.dp).padding(end = 4.dp))
                            Text("Settings")
                        }
                    }
                }

            }
        }
    }

    addTargetSectionId?.let { sectionId ->
        AddEditTileSheet(
            initial = null,
            defaultSectionId = sectionId,
            allSections = sections.map { it.section },
            onSave = { tile ->
                vm.addTile(tile, rows)
                addTargetSectionId = null
            },
            onDismiss = { addTargetSectionId = null }
        )
    }

    editingTile?.let { tile ->
        AddEditTileSheet(
            initial = tile,
            defaultSectionId = tile.sectionId,
            allSections = sections.map { it.section },
            onSave   = { updated -> vm.updateTile(updated); editingTile = null },
            onDelete = { vm.deleteTile(tile); editingTile = null },
            onMove   = { targetId -> vm.moveTile(tile.id, targetId); editingTile = null },
            onDismiss = { editingTile = null }
        )
    }
}

@Composable
private fun PortalBottomBar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
    editMode: Boolean,
    onAdd: () -> Unit,
    onDoneEditing: () -> Unit,
    onSettings: () -> Unit,
    onInfo: () -> Unit,
    colorBack: Color,
    colorSettings: Color,
    colorAdd: Color,
    colorInfo: Color,
    colorForward: Color,
    iconBack: String = "",
    iconSettings: String = "",
    iconAdd: String = "",
    iconInfo: String = "",
    iconForward: String = "",
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val tileSize = maxWidth / 5
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(0.dp)) {
            BottomBarTile(Icons.AutoMirrored.Outlined.ArrowBack,    colorBack,     tileSize, canGoBack,    onBack,        iconBack)
            BottomBarTile(Icons.Outlined.Settings,                  colorSettings, tileSize, true,         onSettings,    iconSettings)
            BottomBarTile(
                icon       = if (editMode) Icons.Outlined.Check else Icons.Outlined.Add,
                bgColor    = colorAdd,
                size       = tileSize,
                onClick    = if (editMode) onDoneEditing else onAdd,
                iconAsset  = if (editMode) "" else iconAdd
            )
            BottomBarTile(Icons.Outlined.Info,                      colorInfo,     tileSize, true,         onInfo,        iconInfo)
            BottomBarTile(Icons.AutoMirrored.Outlined.ArrowForward, colorForward,  tileSize, canGoForward, onForward,     iconForward)
        }
    }
}

@Composable
private fun BottomBarTile(
    icon: ImageVector,
    bgColor: Color,
    size: Dp,
    enabled: Boolean = true,
    onClick: () -> Unit,
    iconAsset: String = "",
) {
    val bgAlpha   = if (enabled) 1f else 0.4f
    val iconAlpha = if (enabled) 1f else 0.55f
    Box(
        modifier = Modifier
            .size(size)
            .background(bgColor.copy(alpha = bgAlpha))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (iconAsset.isNotEmpty()) {
            AsyncImage(
                model              = "file:///android_asset/$iconAsset",
                contentDescription = null,
                modifier           = Modifier
                    .size(size * 0.45f)
                    .alpha(iconAlpha)
            )
        } else {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                tint               = Color.White.copy(alpha = iconAlpha),
                modifier           = Modifier.size(size * 0.45f)
            )
        }
    }
}
