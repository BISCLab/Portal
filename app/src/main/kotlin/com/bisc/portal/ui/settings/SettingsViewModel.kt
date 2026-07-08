package com.bisc.portal.ui.settings

import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bisc.portal.data.model.Section
import com.bisc.portal.data.model.Tile
import com.bisc.portal.data.model.TileSize
import com.bisc.portal.data.repository.PortalRepository
import com.bisc.portal.ui.home.HomePrefKeys
import com.bisc.portal.ui.theme.DefaultAddColor
import com.bisc.portal.ui.theme.DefaultBackColor
import com.bisc.portal.ui.theme.DefaultForwardColor
import com.bisc.portal.ui.theme.DefaultInfoColor
import com.bisc.portal.ui.theme.DefaultSettingsColor
import com.bisc.portal.ui.theme.ThemePreference
import com.bisc.portal.util.SecurityUtil
import com.bisc.portal.util.hexToColor
import com.bisc.portal.util.toHexString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

object PrefKeys {
    val THEME = stringPreferencesKey("theme")
}

object LockPrefKeys {
    val ENABLED    = booleanPreferencesKey("lock_enabled")
    val HASH       = stringPreferencesKey("lock_hash")
    val SALT       = stringPreferencesKey("lock_salt")
    val RESET_HASH = stringPreferencesKey("lock_reset_hash")
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val repo: PortalRepository
) : ViewModel() {

    val theme = dataStore.data.map { prefs ->
        when (prefs[PrefKeys.THEME]) {
            "DARK"  -> ThemePreference.DARK
            "LIGHT" -> ThemePreference.LIGHT
            else    -> ThemePreference.SYSTEM
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, ThemePreference.SYSTEM)

    val bottomSeparator = dataStore.data.map { it[HomePrefKeys.BOTTOM_SEPARATOR] ?: 0f }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    val editLock = dataStore.data.map { it[HomePrefKeys.EDIT_LOCK] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val infiniteScroll = dataStore.data.map { it[HomePrefKeys.INFINITE_SCROLL] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val autoCollapse = dataStore.data.map { it[HomePrefKeys.AUTO_COLLAPSE] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val columns = dataStore.data.map { it[HomePrefKeys.COLUMNS] ?: 5 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 5)

    val rows = dataStore.data.map { it[HomePrefKeys.ROWS] ?: 9 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 9)

    val horizontalWrap = dataStore.data.map { it[HomePrefKeys.HORIZONTAL_WRAP] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val topSeparator = dataStore.data.map { it[HomePrefKeys.TOP_SEPARATOR] ?: 0f }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    val leftSeparator = dataStore.data.map { it[HomePrefKeys.LEFT_SEPARATOR] ?: 0f }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    val rightSeparator = dataStore.data.map { it[HomePrefKeys.RIGHT_SEPARATOR] ?: 0f }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    val tileGap = dataStore.data.map { it[HomePrefKeys.TILE_GAP] ?: 0f }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0f)

    val autoInvertIcons = dataStore.data.map { it[HomePrefKeys.AUTO_INVERT_ICONS] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val browserPackage = dataStore.data.map { it[HomePrefKeys.BROWSER_PACKAGE] ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun setBrowserPackage(pkg: String) = viewModelScope.launch {
        dataStore.edit { it[HomePrefKeys.BROWSER_PACKAGE] = pkg }
    }

    val barShowGesture = dataStore.data.map { it[HomePrefKeys.BAR_SHOW_GESTURE] ?: "tap" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "tap")

    val barMode = dataStore.data.map { it[HomePrefKeys.BAR_MODE] ?: "collapsible" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "collapsible")

    val sections = repo.sectionsWithTiles
        .map { list -> list.sortedBy { it.section.position } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

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

    fun setAutoCollapse(on: Boolean) = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.AUTO_COLLAPSE] = on } }

    fun setColumns(n: Int) = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.COLUMNS] = n } }
    fun setRows(n: Int)    = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.ROWS] = n } }

    fun setHorizontalWrap(v: Boolean)   = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.HORIZONTAL_WRAP] = v } }

    fun setBarShowGesture(gesture: String) = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.BAR_SHOW_GESTURE] = gesture } }

    fun setBarMode(mode: String) = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.BAR_MODE] = mode } }

    val statsEnabled = dataStore.data.map { it[HomePrefKeys.STATS_ENABLED] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun setStatsEnabled(on: Boolean) = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.STATS_ENABLED] = on } }

    val arrowDimAtEnds = dataStore.data.map { it[HomePrefKeys.BACK_DISABLED_GREY] ?: true }
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun setArrowDimAtEnds(on: Boolean) = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.BACK_DISABLED_GREY] = on } }

    fun setBtnColorBack(c: Color)     = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.BTN_COLOR_BACK]     = c.toHexString() } }
    fun setBtnColorSettings(c: Color) = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.BTN_COLOR_SETTINGS] = c.toHexString() } }
    fun setBtnColorAdd(c: Color)      = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.BTN_COLOR_ADD]      = c.toHexString() } }
    fun setBtnColorInfo(c: Color)     = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.BTN_COLOR_INFO]     = c.toHexString() } }
    fun setBtnColorForward(c: Color)  = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.BTN_COLOR_FORWARD]  = c.toHexString() } }

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

    fun setBtnIconBack(a: String)     = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.BTN_ICON_BACK]     = a } }
    fun setBtnIconSettings(a: String) = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.BTN_ICON_SETTINGS] = a } }
    fun setBtnIconAdd(a: String)      = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.BTN_ICON_ADD]      = a } }
    fun setBtnIconInfo(a: String)     = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.BTN_ICON_INFO]     = a } }
    fun setBtnIconForward(a: String)  = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.BTN_ICON_FORWARD]  = a } }

    fun setTheme(p: ThemePreference) = viewModelScope.launch {
        dataStore.edit { it[PrefKeys.THEME] = p.name }
    }

    fun setBottomSeparator(v: Float) = viewModelScope.launch {
        dataStore.edit { it[HomePrefKeys.BOTTOM_SEPARATOR] = v }
    }

    fun setTopSeparator(v: Float)    = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.TOP_SEPARATOR]    = v } }
    fun setLeftSeparator(v: Float)   = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.LEFT_SEPARATOR]   = v } }
    fun setRightSeparator(v: Float)  = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.RIGHT_SEPARATOR]  = v } }
    fun setTileGap(v: Float)         = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.TILE_GAP]         = v } }
    fun setAutoInvertIcons(on: Boolean) = viewModelScope.launch { dataStore.edit { it[HomePrefKeys.AUTO_INVERT_ICONS] = on } }

    fun setEditLock(locked: Boolean) = viewModelScope.launch {
        dataStore.edit { it[HomePrefKeys.EDIT_LOCK] = locked }
    }

    fun setInfiniteScroll(on: Boolean) = viewModelScope.launch {
        dataStore.edit { it[HomePrefKeys.INFINITE_SCROLL] = on }
    }

    fun addSection(name: String) = viewModelScope.launch { repo.addSection(name) }
    fun updateSection(s: Section) = viewModelScope.launch { repo.updateSection(s) }
    fun deleteSection(s: Section) = viewModelScope.launch { repo.deleteSection(s) }

    val lockEnabled = dataStore.data.map { it[LockPrefKeys.ENABLED] ?: false }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val lockSalt = dataStore.data.map { it[LockPrefKeys.SALT] ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")
    private val lockHash = dataStore.data.map { it[LockPrefKeys.HASH] ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")
    private val lockResetHash = dataStore.data.map { it[LockPrefKeys.RESET_HASH] ?: "" }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun verifyPassword(input: String): Boolean {
        val salt = lockSalt.value
        val hash = lockHash.value
        return salt.isNotEmpty() && hash.isNotEmpty() && SecurityUtil.hashPassword(input, salt) == hash
    }

    fun verifyResetCode(input: String): Boolean {
        val resetHash = lockResetHash.value
        return resetHash.isNotEmpty() && SecurityUtil.hashResetCode(input) == resetHash
    }

    fun setupLock(password: String, onReady: (resetCode: String) -> Unit) = viewModelScope.launch {
        val salt = SecurityUtil.generateSalt()
        val hash = SecurityUtil.hashPassword(password, salt)
        val resetCode = SecurityUtil.generateResetCode()
        val resetHash = SecurityUtil.hashResetCode(resetCode)
        dataStore.edit { prefs ->
            prefs[LockPrefKeys.ENABLED] = true
            prefs[LockPrefKeys.SALT] = salt
            prefs[LockPrefKeys.HASH] = hash
            prefs[LockPrefKeys.RESET_HASH] = resetHash
        }
        onReady(resetCode)
    }

    fun changeLockPassword(newPassword: String, onReady: (resetCode: String) -> Unit) = viewModelScope.launch {
        val salt = SecurityUtil.generateSalt()
        val hash = SecurityUtil.hashPassword(newPassword, salt)
        val resetCode = SecurityUtil.generateResetCode()
        val resetHash = SecurityUtil.hashResetCode(resetCode)
        dataStore.edit { prefs ->
            prefs[LockPrefKeys.SALT] = salt
            prefs[LockPrefKeys.HASH] = hash
            prefs[LockPrefKeys.RESET_HASH] = resetHash
        }
        onReady(resetCode)
    }

    fun disableLock() = viewModelScope.launch {
        dataStore.edit { prefs ->
            prefs[LockPrefKeys.ENABLED] = false
            prefs.remove(LockPrefKeys.SALT)
            prefs.remove(LockPrefKeys.HASH)
            prefs.remove(LockPrefKeys.RESET_HASH)
        }
    }

    suspend fun buildExportJson(): String {
        val sections = repo.getAllSections()
        val tiles = repo.getAllTiles()
        val prefs = dataStore.data.first()

        val root = JSONObject()
        root.put("version", 1)

        val sectionsArr = JSONArray()
        sections.forEach { s ->
            sectionsArr.put(JSONObject().apply {
                put("id", s.id); put("name", s.name); put("showName", s.showName)
                put("position", s.position); put("headerBgColor", s.headerBgColor)
                put("headerIcon", s.headerIcon); put("headerHeight", s.headerHeight)
                put("sectionColumns", s.sectionColumns); put("headerRadius", s.headerRadius)
                put("headerGridCol", s.headerGridCol); put("headerGridRow", s.headerGridRow)
                put("headerColSpan", s.headerColSpan); put("headerRowSpan", s.headerRowSpan)
                put("gridPreset", s.gridPreset)
            })
        }
        root.put("sections", sectionsArr)

        val tilesArr = JSONArray()
        tiles.forEach { t ->
            tilesArr.put(JSONObject().apply {
                put("id", t.id); put("sectionId", t.sectionId); put("label", t.label)
                put("showLabel", t.showLabel); put("url", t.url); put("iconAsset", t.iconAsset)
                put("iconUri", t.iconUri); put("size", t.size.name); put("position", t.position)
                put("gridCol", t.gridCol); put("gridRow", t.gridRow); put("colSpan", t.colSpan)
                put("rowSpan", t.rowSpan); put("iconShape", t.iconShape); put("iconScale", t.iconScale)
                put("iconBgColor", t.iconBgColor); put("iconBgEnabled", t.iconBgEnabled)
                put("invertIcon", t.invertIcon); put("autoInvertIcon", t.autoInvertIcon)
                put("isTextTile", t.isTextTile); put("labelColor", t.labelColor)
                put("labelAlign", t.labelAlign); put("labelFontSize", t.labelFontSize)
                put("isIconTextTile", t.isIconTextTile); put("iconTextPosition", t.iconTextPosition)
                put("labelBold", t.labelBold); put("labelItalic", t.labelItalic)
                put("iconZoom", t.iconZoom)
            })
        }
        root.put("tiles", tilesArr)

        val settings = JSONObject()
        prefs[PrefKeys.THEME]?.let { settings.put("theme", it) }
        prefs[HomePrefKeys.COLUMNS]?.let { settings.put("columns", it) }
        prefs[HomePrefKeys.ROWS]?.let { settings.put("rows", it) }
        prefs[HomePrefKeys.TILE_GAP]?.let { settings.put("tile_gap", it) }
        prefs[HomePrefKeys.TOP_SEPARATOR]?.let { settings.put("top_separator", it) }
        prefs[HomePrefKeys.BOTTOM_SEPARATOR]?.let { settings.put("bottom_separator", it) }
        prefs[HomePrefKeys.LEFT_SEPARATOR]?.let { settings.put("left_separator", it) }
        prefs[HomePrefKeys.RIGHT_SEPARATOR]?.let { settings.put("right_separator", it) }
        prefs[HomePrefKeys.EDIT_LOCK]?.let { settings.put("edit_lock", it) }
        prefs[HomePrefKeys.INFINITE_SCROLL]?.let { settings.put("infinite_scroll", it) }
        prefs[HomePrefKeys.AUTO_COLLAPSE]?.let { settings.put("auto_collapse", it) }
        prefs[HomePrefKeys.BAR_MODE]?.let { settings.put("bar_mode", it) }
        prefs[HomePrefKeys.BAR_SHOW_GESTURE]?.let { settings.put("bar_show_gesture", it) }
        prefs[HomePrefKeys.BACK_DISABLED_GREY]?.let { settings.put("back_disabled_grey", it) }
        prefs[HomePrefKeys.HORIZONTAL_WRAP]?.let { settings.put("horizontal_wrap", it) }
        prefs[HomePrefKeys.AUTO_INVERT_ICONS]?.let { settings.put("auto_invert_icons", it) }
        prefs[HomePrefKeys.BROWSER_PACKAGE]?.let { settings.put("browser_package", it) }
        prefs[HomePrefKeys.BTN_COLOR_BACK]?.let { settings.put("btn_color_back", it) }
        prefs[HomePrefKeys.BTN_COLOR_SETTINGS]?.let { settings.put("btn_color_settings", it) }
        prefs[HomePrefKeys.BTN_COLOR_ADD]?.let { settings.put("btn_color_add", it) }
        prefs[HomePrefKeys.BTN_COLOR_INFO]?.let { settings.put("btn_color_info", it) }
        prefs[HomePrefKeys.BTN_COLOR_FORWARD]?.let { settings.put("btn_color_forward", it) }
        prefs[HomePrefKeys.STATS_ENABLED]?.let { settings.put("stats_enabled", it) }
        prefs[HomePrefKeys.BTN_ICON_BACK]?.let { settings.put("btn_icon_back", it) }
        prefs[HomePrefKeys.BTN_ICON_SETTINGS]?.let { settings.put("btn_icon_settings", it) }
        prefs[HomePrefKeys.BTN_ICON_ADD]?.let { settings.put("btn_icon_add", it) }
        prefs[HomePrefKeys.BTN_ICON_INFO]?.let { settings.put("btn_icon_info", it) }
        prefs[HomePrefKeys.BTN_ICON_FORWARD]?.let { settings.put("btn_icon_forward", it) }
        root.put("settings", settings)

        return root.toString(2)
    }

    suspend fun importJson(json: String): Boolean {
        return try {
            val root = JSONObject(json)
            val sectionsArr = root.getJSONArray("sections")
            val tilesArr = root.getJSONArray("tiles")

            val sections = (0 until sectionsArr.length()).map { i ->
                val o = sectionsArr.getJSONObject(i)
                Section(
                    id = o.getLong("id"), name = o.getString("name"),
                    showName = o.optBoolean("showName", false),
                    position = o.getInt("position"),
                    headerBgColor = o.optString("headerBgColor", "000B29"),
                    headerIcon = o.optString("headerIcon", ""),
                    headerHeight = o.optInt("headerHeight", 1),
                    sectionColumns = o.optInt("sectionColumns", 0),
                    headerRadius = o.optInt("headerRadius", 0),
                    headerGridCol = o.optInt("headerGridCol", 0),
                    headerGridRow = o.optInt("headerGridRow", 0),
                    headerColSpan = o.optInt("headerColSpan", 2),
                    headerRowSpan = o.optInt("headerRowSpan", 10),
                    gridPreset = o.optInt("gridPreset", 0)
                )
            }

            val tiles = (0 until tilesArr.length()).map { i ->
                val o = tilesArr.getJSONObject(i)
                Tile(
                    id = o.getLong("id"), sectionId = o.getLong("sectionId"),
                    label = o.optString("label", ""), showLabel = o.optBoolean("showLabel", false),
                    url = o.optString("url", ""), iconAsset = o.optString("iconAsset", ""),
                    iconUri = o.optString("iconUri", ""),
                    size = runCatching { TileSize.valueOf(o.optString("size", "SQUARE")) }.getOrDefault(TileSize.SQUARE),
                    position = o.optInt("position", 0), gridCol = o.optInt("gridCol", 0),
                    gridRow = o.optInt("gridRow", 0), colSpan = o.optInt("colSpan", 2),
                    rowSpan = o.optInt("rowSpan", 2), iconShape = o.optString("iconShape", "square"),
                    iconScale = o.optString("iconScale", "crop"), iconBgColor = o.optString("iconBgColor", ""),
                    iconBgEnabled = o.optBoolean("iconBgEnabled", false),
                    invertIcon = o.optBoolean("invertIcon", false),
                    autoInvertIcon = o.optBoolean("autoInvertIcon", false),
                    isTextTile = o.optBoolean("isTextTile", false),
                    labelColor = o.optString("labelColor", "FFFFFF"),
                    labelAlign = o.optInt("labelAlign", 0), labelFontSize = o.optInt("labelFontSize", 0),
                    isIconTextTile = o.optBoolean("isIconTextTile", false),
                    iconTextPosition = o.optInt("iconTextPosition", 0),
                    labelBold = o.optBoolean("labelBold", false),
                    labelItalic = o.optBoolean("labelItalic", false),
                    iconZoom = o.optDouble("iconZoom", 1.0).toFloat()
                )
            }

            repo.clearAndRestore(sections, tiles)

            if (root.has("settings")) {
                val s = root.getJSONObject("settings")
                dataStore.edit { prefs ->
                    if (s.has("theme")) prefs[PrefKeys.THEME] = s.getString("theme")
                    if (s.has("columns")) prefs[HomePrefKeys.COLUMNS] = s.getInt("columns")
                    if (s.has("rows")) prefs[HomePrefKeys.ROWS] = s.getInt("rows")
                    if (s.has("tile_gap")) prefs[HomePrefKeys.TILE_GAP] = s.getDouble("tile_gap").toFloat()
                    if (s.has("top_separator")) prefs[HomePrefKeys.TOP_SEPARATOR] = s.getDouble("top_separator").toFloat()
                    if (s.has("bottom_separator")) prefs[HomePrefKeys.BOTTOM_SEPARATOR] = s.getDouble("bottom_separator").toFloat()
                    if (s.has("left_separator")) prefs[HomePrefKeys.LEFT_SEPARATOR] = s.getDouble("left_separator").toFloat()
                    if (s.has("right_separator")) prefs[HomePrefKeys.RIGHT_SEPARATOR] = s.getDouble("right_separator").toFloat()
                    if (s.has("edit_lock")) prefs[HomePrefKeys.EDIT_LOCK] = s.getBoolean("edit_lock")
                    if (s.has("infinite_scroll")) prefs[HomePrefKeys.INFINITE_SCROLL] = s.getBoolean("infinite_scroll")
                    if (s.has("auto_collapse")) prefs[HomePrefKeys.AUTO_COLLAPSE] = s.getBoolean("auto_collapse")
                    if (s.has("bar_mode")) prefs[HomePrefKeys.BAR_MODE] = s.getString("bar_mode")
                    if (s.has("bar_show_gesture")) prefs[HomePrefKeys.BAR_SHOW_GESTURE] = s.getString("bar_show_gesture")
                    if (s.has("back_disabled_grey")) prefs[HomePrefKeys.BACK_DISABLED_GREY] = s.getBoolean("back_disabled_grey")
                    if (s.has("horizontal_wrap")) prefs[HomePrefKeys.HORIZONTAL_WRAP] = s.getBoolean("horizontal_wrap")
                    if (s.has("auto_invert_icons")) prefs[HomePrefKeys.AUTO_INVERT_ICONS] = s.getBoolean("auto_invert_icons")
                    if (s.has("browser_package")) prefs[HomePrefKeys.BROWSER_PACKAGE] = s.getString("browser_package")
                    if (s.has("btn_color_back")) prefs[HomePrefKeys.BTN_COLOR_BACK] = s.getString("btn_color_back")
                    if (s.has("btn_color_settings")) prefs[HomePrefKeys.BTN_COLOR_SETTINGS] = s.getString("btn_color_settings")
                    if (s.has("btn_color_add")) prefs[HomePrefKeys.BTN_COLOR_ADD] = s.getString("btn_color_add")
                    if (s.has("btn_color_info")) prefs[HomePrefKeys.BTN_COLOR_INFO] = s.getString("btn_color_info")
                    if (s.has("btn_color_forward")) prefs[HomePrefKeys.BTN_COLOR_FORWARD] = s.getString("btn_color_forward")
                    if (s.has("stats_enabled")) prefs[HomePrefKeys.STATS_ENABLED] = s.getBoolean("stats_enabled")
                    if (s.has("btn_icon_back"))     prefs[HomePrefKeys.BTN_ICON_BACK]     = s.getString("btn_icon_back")
                    if (s.has("btn_icon_settings")) prefs[HomePrefKeys.BTN_ICON_SETTINGS] = s.getString("btn_icon_settings")
                    if (s.has("btn_icon_add"))      prefs[HomePrefKeys.BTN_ICON_ADD]      = s.getString("btn_icon_add")
                    if (s.has("btn_icon_info"))     prefs[HomePrefKeys.BTN_ICON_INFO]     = s.getString("btn_icon_info")
                    if (s.has("btn_icon_forward"))  prefs[HomePrefKeys.BTN_ICON_FORWARD]  = s.getString("btn_icon_forward")
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
