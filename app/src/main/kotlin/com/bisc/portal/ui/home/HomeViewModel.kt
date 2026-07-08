package com.bisc.portal.ui.home

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bisc.portal.data.model.Section
import com.bisc.portal.data.model.Tile
import com.bisc.portal.data.repository.PortalRepository
import com.bisc.portal.ui.theme.DefaultBackColor
import com.bisc.portal.ui.theme.DefaultSettingsColor
import com.bisc.portal.ui.theme.DefaultAddColor
import com.bisc.portal.ui.theme.DefaultInfoColor
import com.bisc.portal.ui.theme.DefaultForwardColor
import com.bisc.portal.util.hexToColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

object HomePrefKeys {
    val COLUMNS          = intPreferencesKey("columns")
    val TILE_GAP         = floatPreferencesKey("tile_gap")
    val OUTER_PADDING    = floatPreferencesKey("outer_padding")
    val BOTTOM_SEPARATOR = floatPreferencesKey("bottom_separator")
    val EDIT_LOCK        = booleanPreferencesKey("edit_lock")
    val INFINITE_SCROLL  = booleanPreferencesKey("infinite_scroll")
    val BTN_COLOR_BACK     = stringPreferencesKey("btn_color_back")
    val BTN_COLOR_SETTINGS = stringPreferencesKey("btn_color_settings")
    val BTN_COLOR_ADD      = stringPreferencesKey("btn_color_add")
    val BTN_COLOR_INFO     = stringPreferencesKey("btn_color_info")
    val BTN_COLOR_FORWARD  = stringPreferencesKey("btn_color_forward")
    val AUTO_COLLAPSE        = booleanPreferencesKey("auto_collapse")
    val BAR_SHOW_GESTURE     = stringPreferencesKey("bar_show_gesture")
    val BAR_MODE             = stringPreferencesKey("bar_mode")
    val STATS_ENABLED        = booleanPreferencesKey("stats_enabled")
    val ROWS                 = intPreferencesKey("rows_per_page")
    val BACK_DISABLED_GREY   = booleanPreferencesKey("back_disabled_grey")
    val HORIZONTAL_WRAP      = booleanPreferencesKey("horizontal_wrap")
    val TOP_SEPARATOR        = floatPreferencesKey("top_separator")
    val LEFT_SEPARATOR       = floatPreferencesKey("left_separator")
    val RIGHT_SEPARATOR      = floatPreferencesKey("right_separator")
    val AUTO_INVERT_ICONS    = booleanPreferencesKey("auto_invert_icons")
    val BROWSER_PACKAGE      = stringPreferencesKey("browser_package")
    val BTN_ICON_BACK        = stringPreferencesKey("btn_icon_back")
    val BTN_ICON_SETTINGS    = stringPreferencesKey("btn_icon_settings")
    val BTN_ICON_ADD         = stringPreferencesKey("btn_icon_add")
    val BTN_ICON_INFO        = stringPreferencesKey("btn_icon_info")
    val BTN_ICON_FORWARD     = stringPreferencesKey("btn_icon_forward")
}

sealed class HomeEvent {
    object ScreenFull : HomeEvent()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: PortalRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    val sectionsWithTiles = repo.sectionsWithTiles
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // columns kept for grid-unit calculation even though slider is hidden from settings
    val columns = dataStore.data.map { it[HomePrefKeys.COLUMNS] ?: 5 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 5)

    val tileGapDp = dataStore.data.map { it[HomePrefKeys.TILE_GAP] ?: 0f }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    val outerPaddingDp = dataStore.data.map { it[HomePrefKeys.OUTER_PADDING] ?: 0f }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    val bottomSeparatorDp = dataStore.data.map { it[HomePrefKeys.BOTTOM_SEPARATOR] ?: 0f }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    val editLock = dataStore.data.map { it[HomePrefKeys.EDIT_LOCK] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val infiniteScroll = dataStore.data.map { it[HomePrefKeys.INFINITE_SCROLL] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val arrowDimAtEnds = dataStore.data.map { it[HomePrefKeys.BACK_DISABLED_GREY] ?: true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val btnIconBack     = dataStore.data.map { it[HomePrefKeys.BTN_ICON_BACK] ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val btnIconSettings = dataStore.data.map { it[HomePrefKeys.BTN_ICON_SETTINGS] ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val btnIconAdd      = dataStore.data.map { it[HomePrefKeys.BTN_ICON_ADD] ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val btnIconInfo     = dataStore.data.map { it[HomePrefKeys.BTN_ICON_INFO] ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val btnIconForward  = dataStore.data.map { it[HomePrefKeys.BTN_ICON_FORWARD] ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val btnColorBack = dataStore.data.map { it[HomePrefKeys.BTN_COLOR_BACK]?.hexToColor() ?: DefaultBackColor }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DefaultBackColor)
    val btnColorSettings = dataStore.data.map { it[HomePrefKeys.BTN_COLOR_SETTINGS]?.hexToColor() ?: DefaultSettingsColor }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DefaultSettingsColor)
    val btnColorAdd = dataStore.data.map { it[HomePrefKeys.BTN_COLOR_ADD]?.hexToColor() ?: DefaultAddColor }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DefaultAddColor)
    val btnColorInfo = dataStore.data.map { it[HomePrefKeys.BTN_COLOR_INFO]?.hexToColor() ?: DefaultInfoColor }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DefaultInfoColor)
    val btnColorForward = dataStore.data.map { it[HomePrefKeys.BTN_COLOR_FORWARD]?.hexToColor() ?: DefaultForwardColor }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DefaultForwardColor)

    val autoCollapse = dataStore.data.map { it[HomePrefKeys.AUTO_COLLAPSE] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val barShowGesture = dataStore.data.map { it[HomePrefKeys.BAR_SHOW_GESTURE] ?: "tap" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "tap")

    val barMode = dataStore.data.map { it[HomePrefKeys.BAR_MODE] ?: "collapsible" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "collapsible")

    val statsEnabled = dataStore.data.map { it[HomePrefKeys.STATS_ENABLED] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val rows = dataStore.data.map { it[HomePrefKeys.ROWS] ?: 9 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 9)

    val horizontalWrap = dataStore.data.map { it[HomePrefKeys.HORIZONTAL_WRAP] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val topSeparatorDp = dataStore.data.map { it[HomePrefKeys.TOP_SEPARATOR] ?: 0f }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    val leftSeparatorDp = dataStore.data.map { it[HomePrefKeys.LEFT_SEPARATOR] ?: 0f }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    val rightSeparatorDp = dataStore.data.map { it[HomePrefKeys.RIGHT_SEPARATOR] ?: 0f }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    val autoInvertIcons = dataStore.data.map { it[HomePrefKeys.AUTO_INVERT_ICONS] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val browserPackage = dataStore.data.map { it[HomePrefKeys.BROWSER_PACKAGE] ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun recordClick(tile: Tile) = viewModelScope.launch {
        if (statsEnabled.value) repo.recordClick(tile.id, tile.url, tile.label)
    }

    private val _events = MutableSharedFlow<HomeEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<HomeEvent> = _events

    init {
        viewModelScope.launch { repo.ensureDefaultSection() }
    }

    fun addTile(tile: Tile, maxRows: Int = Int.MAX_VALUE) = viewModelScope.launch {
        val placed = repo.addTile(tile, columns.value * 2, maxRows)
        if (!placed) _events.tryEmit(HomeEvent.ScreenFull)
    }

    fun updateSection(section: Section) = viewModelScope.launch { repo.updateSection(section) }

    fun updateTile(tile: Tile) = viewModelScope.launch { repo.updateTile(tile) }

    fun deleteTile(tile: Tile) = viewModelScope.launch { repo.deleteTile(tile) }

    fun moveTile(tileId: Long, targetSectionId: Long) =
        viewModelScope.launch { repo.moveTile(tileId, targetSectionId) }

    fun setTileGridPos(tileId: Long, col: Int, row: Int, internalCols: Int = -1) = viewModelScope.launch {
        val tile = sectionsWithTiles.value.flatMap { it.tiles }.find { it.id == tileId }
            ?: return@launch
        val effCols = if (internalCols > 0) internalCols else columns.value * 2
        repo.setTileGridPos(tile, col, row, effCols)
    }

    fun setTileSpan(tileId: Long, colSpan: Int, rowSpan: Int) = viewModelScope.launch {
        repo.setTileSpan(tileId, colSpan, rowSpan)
    }

    fun setTilePosAndSpan(tileId: Long, col: Int, row: Int, colSpan: Int, rowSpan: Int) = viewModelScope.launch {
        repo.setTilePosAndSpan(tileId, col, row, colSpan, rowSpan)
    }
}
