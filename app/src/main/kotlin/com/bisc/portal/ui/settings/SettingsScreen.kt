package com.bisc.portal.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.bisc.portal.data.model.Section
import com.bisc.portal.ui.sheet.HsvColorPicker
import com.bisc.portal.ui.sheet.IconPickerSheet
import com.bisc.portal.ui.theme.NeonRed
import com.bisc.portal.ui.theme.NightBlue
import com.bisc.portal.ui.theme.ThemePreference
import com.bisc.portal.util.hexToColor
import com.bisc.portal.util.toHexString
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    vm: SettingsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToStats: () -> Unit = {},
    onLockEnabled: () -> Unit = {}
) {
    //region Screen state
    val theme           by vm.theme.collectAsState()
    val bottomSeparator by vm.bottomSeparator.collectAsState()
    val topSeparator    by vm.topSeparator.collectAsState()
    val leftSeparator   by vm.leftSeparator.collectAsState()
    val rightSeparator  by vm.rightSeparator.collectAsState()
    val editLock        by vm.editLock.collectAsState()
    val infiniteScroll  by vm.infiniteScroll.collectAsState()
    val autoCollapse    by vm.autoCollapse.collectAsState()
    val columns         by vm.columns.collectAsState()
    val rows            by vm.rows.collectAsState()
    val barShowGesture  by vm.barShowGesture.collectAsState()
    val barMode         by vm.barMode.collectAsState()
    val statsEnabled    by vm.statsEnabled.collectAsState()
    val sections          by vm.sections.collectAsState()
    val browserPackage    by vm.browserPackage.collectAsState()
    val btnColorBack     by vm.btnColorBack.collectAsState()
    val btnColorSettings by vm.btnColorSettings.collectAsState()
    val btnColorAdd      by vm.btnColorAdd.collectAsState()
    val btnColorInfo     by vm.btnColorInfo.collectAsState()
    val btnColorForward  by vm.btnColorForward.collectAsState()
    val horizontalWrap   by vm.horizontalWrap.collectAsState()
    val tileGap          by vm.tileGap.collectAsState()
    val arrowDimAtEnds   by vm.arrowDimAtEnds.collectAsState()
    val autoInvertIcons  by vm.autoInvertIcons.collectAsState()
    val btnIconBack      by vm.btnIconBack.collectAsState()
    val btnIconSettings  by vm.btnIconSettings.collectAsState()
    val btnIconAdd       by vm.btnIconAdd.collectAsState()
    val btnIconInfo      by vm.btnIconInfo.collectAsState()
    val btnIconForward   by vm.btnIconForward.collectAsState()
    val lockEnabled      by vm.lockEnabled.collectAsState()

    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    var addSectionName    by remember { mutableStateOf("") }
    var showAddSection    by remember { mutableStateOf(false) }
    var colorPickerTarget by remember { mutableStateOf<String?>(null) }
    var iconPickerTarget  by remember { mutableStateOf<String?>(null) }

    var backupMessage by remember { mutableStateOf("") }

    // App lock dialog state:
    // 0=none 1=setup-enter 2=setup-confirm 3=show-reset-code
    // 4=disable-verify 5=change-verify 6=change-new-pw 7=change-reset-code
    var lockPhase     by remember { mutableStateOf(0) }
    var lockPwInput   by remember { mutableStateOf("") }
    var lockPwConfirm by remember { mutableStateOf("") }
    var lockPwError   by remember { mutableStateOf("") }
    var lockPwVisible by remember { mutableStateOf(false) }
    var lockResetCode by remember { mutableStateOf("") }

    fun resetLockDialog() { lockPhase = 0; lockPwInput = ""; lockPwConfirm = ""; lockPwError = ""; lockPwVisible = false }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val json = vm.buildExportJson()
                    context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
                    backupMessage = "Export successful."
                } catch (e: Exception) {
                    backupMessage = "Export failed: ${e.message}"
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val json = context.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
                        ?: throw IllegalStateException("Could not read file")
                    val ok = vm.importJson(json)
                    backupMessage = if (ok) "Import successful. Restart may be needed." else "Import failed: invalid file."
                } catch (e: Exception) {
                    backupMessage = "Import failed: ${e.message}"
                }
            }
        }
    }

    //endregion

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background,
                )
            )
        }
    ) { pad ->
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp, end = 16.dp,
                top = pad.calculateTopPadding() + 4.dp,
                bottom = pad.calculateBottomPadding() + 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            //region Appearance
            item {
                SettingsSection(
                    icon = Icons.Outlined.Brush,
                    iconBg = NeonRed,
                    title = "Appearance",
                    subtitle = "Theme, colors and icon inversion"
                ) {
                    ThemeItem(theme) { vm.setTheme(it) }
                    SettingsDivider()
                    ToggleItem(
                        title = "Auto-invert icons in dark mode",
                        subtitle = "Globally inverts all icon colors when the system is in dark mode",
                        checked = autoInvertIcons,
                        onToggle = { vm.setAutoInvertIcons(it) }
                    )
                }
            }
            //endregion

            //region Grid layout
            item {
                SettingsSection(
                    icon = Icons.Outlined.Tune,
                    iconBg = Color(0xFF3D4F73),
                    title = "Grid Layout",
                    subtitle = "Spacing, margins and lock"
                ) {
                    ToggleItem(
                        title = "Lock layout",
                        subtitle = "Disable drag-to-move and resize",
                        checked = editLock,
                        onToggle = { vm.setEditLock(it) }
                    )
                    SettingsDivider()
                    SliderItem(
                        title = "Top margin",
                        valueLabel = "${topSeparator.roundToInt()} dp",
                        value = topSeparator,
                        range = 0f..48f,
                        onValueChange = { vm.setTopSeparator(it) }
                    )
                    SettingsDivider()
                    SliderItem(
                        title = "Bottom margin",
                        valueLabel = "${bottomSeparator.roundToInt()} dp",
                        value = bottomSeparator,
                        range = 0f..48f,
                        onValueChange = { vm.setBottomSeparator(it) }
                    )
                    SettingsDivider()
                    SliderItem(
                        title = "Left margin",
                        valueLabel = "${leftSeparator.roundToInt()} dp",
                        value = leftSeparator,
                        range = 0f..48f,
                        onValueChange = { vm.setLeftSeparator(it) }
                    )
                    SettingsDivider()
                    SliderItem(
                        title = "Right margin",
                        valueLabel = "${rightSeparator.roundToInt()} dp",
                        value = rightSeparator,
                        range = 0f..48f,
                        onValueChange = { vm.setRightSeparator(it) }
                    )
                    SettingsDivider()
                    SliderItem(
                        title = "Tile spacing",
                        valueLabel = "${tileGap.roundToInt()} dp",
                        value = tileGap,
                        range = 0f..16f,
                        onValueChange = { vm.setTileGap(it) }
                    )
                }
            }
            //endregion

            //region Navigation
            item {
                SettingsSection(
                    icon = Icons.AutoMirrored.Outlined.ArrowForward,
                    iconBg = NightBlue,
                    title = "Navigation",
                    subtitle = "Toolbar, scroll, gestures"
                ) {
                    ToggleItem(
                        title = "Infinite vertical scroll",
                        subtitle = "Tiles can extend beyond screen height",
                        checked = infiniteScroll,
                        onToggle = { vm.setInfiniteScroll(it) }
                    )
                    SettingsDivider()
                    ToggleItem(
                        title = "Loop pages horizontally",
                        subtitle = "Back/forward buttons wrap around from last to first page",
                        checked = horizontalWrap,
                        onToggle = { vm.setHorizontalWrap(it) }
                    )
                    SettingsDivider()
                    ToggleItem(
                        title = "Dim arrows at page ends",
                        subtitle = "Back/forward arrows appear faded when there is no further page in that direction",
                        checked = arrowDimAtEnds,
                        onToggle = { vm.setArrowDimAtEnds(it) }
                    )
                    SettingsDivider()
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
                        Text("Toolbar", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            "Always visible, collapsible (swipe down to hide), or completely hidden",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(10.dp))
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            listOf("always" to "Always", "collapsible" to "Collapsible", "hidden" to "Hidden")
                                .forEachIndexed { idx, (value, label) ->
                                    SegmentedButton(
                                        selected = barMode == value,
                                        onClick  = { vm.setBarMode(value) },
                                        shape    = SegmentedButtonDefaults.itemShape(idx, 3),
                                        label    = { Text(label, style = MaterialTheme.typography.bodySmall) }
                                    )
                                }
                        }
                    }
                    AnimatedVisibility(visible = barMode == "collapsible") {
                        Column {
                            SettingsDivider()
                            ToggleItem(
                                title = "Auto-collapse",
                                subtitle = "Toolbar hides automatically after a few seconds",
                                checked = autoCollapse,
                                onToggle = { vm.setAutoCollapse(it) }
                            )
                        }
                    }
                    AnimatedVisibility(visible = barMode == "collapsible" || barMode == "hidden") {
                        Column {
                            SettingsDivider()
                            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
                                Text("Reveal gesture", style = MaterialTheme.typography.bodyLarge)
                                Text(
                                    when (barMode) {
                                        "hidden" -> "Gesture anywhere on the free screen area to temporarily show toolbar (single tap disabled)"
                                        else     -> "Gesture in the bottom toolbar area to show toolbar after swiping it down"
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(10.dp))
                                val gestureOptions = if (barMode == "hidden")
                                    listOf("double_tap" to "Double tap", "swipe_up" to "Swipe up")
                                else
                                    listOf("tap" to "Tap", "double_tap" to "Double tap", "swipe_up" to "Swipe up")
                                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                                    gestureOptions.forEachIndexed { idx, (value, label) ->
                                        SegmentedButton(
                                            selected = barShowGesture == value ||
                                                (barMode == "hidden" && barShowGesture == "tap" && idx == 0),
                                            onClick  = { vm.setBarShowGesture(value) },
                                            shape    = SegmentedButtonDefaults.itemShape(idx, gestureOptions.size),
                                            label    = { Text(label, style = MaterialTheme.typography.bodySmall) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //endregion

            //region Browser
            item {
                val browsers = remember {
                    val probe = Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com"))
                    context.packageManager
                        .queryIntentActivities(probe, PackageManager.MATCH_DEFAULT_ONLY)
                        .map { it.activityInfo.packageName }
                        .distinct()
                }
                SettingsSection(
                    icon = Icons.Outlined.Language,
                    iconBg = Color(0xFF2EAB6C),
                    title = "Browser",
                    subtitle = "Which browser opens your bookmarks"
                ) {
                    BrowserOption(
                        label = "System default",
                        selected = browserPackage.isEmpty(),
                        onClick = { vm.setBrowserPackage("") }
                    )
                    browsers.forEach { pkg ->
                        SettingsDivider()
                        val appName = remember(pkg) {
                            runCatching {
                                context.packageManager
                                    .getApplicationInfo(pkg, 0)
                                    .loadLabel(context.packageManager).toString()
                            }.getOrDefault(pkg)
                        }
                        BrowserOption(
                            label = appName,
                            selected = browserPackage == pkg,
                            onClick = { vm.setBrowserPackage(pkg) }
                        )
                    }
                }
            }
            //endregion

            //region Buttons
            item {
                SettingsSection(
                    icon = Icons.Outlined.Palette,
                    iconBg = Color(0xFF2D8FD4),
                    title = "Buttons",
                    subtitle = "Colors for each button"
                ) {
                    ColorRow(Icons.AutoMirrored.Outlined.ArrowBack,    "Back arrow",    btnColorBack,    btnIconBack,     { iconPickerTarget = "back" },     { vm.setBtnIconBack("") })     { colorPickerTarget = "back" }
                    SettingsDivider()
                    ColorRow(Icons.Outlined.Settings,                  "Settings",      btnColorSettings, btnIconSettings, { iconPickerTarget = "settings" }, { vm.setBtnIconSettings("") }) { colorPickerTarget = "settings" }
                    SettingsDivider()
                    ColorRow(Icons.Outlined.Add,                       "Add / Done",    btnColorAdd,     btnIconAdd,      { iconPickerTarget = "add" },      { vm.setBtnIconAdd("") })      { colorPickerTarget = "add" }
                    SettingsDivider()
                    ColorRow(Icons.Outlined.Info,                      "Info",          btnColorInfo,    btnIconInfo,     { iconPickerTarget = "info" },     { vm.setBtnIconInfo("") })     { colorPickerTarget = "info" }
                    SettingsDivider()
                    ColorRow(Icons.AutoMirrored.Outlined.ArrowForward, "Forward arrow", btnColorForward, btnIconForward,  { iconPickerTarget = "forward" },  { vm.setBtnIconForward("") })  { colorPickerTarget = "forward" }
                }
            }
            //endregion

            //region Pages
            item {
                SettingsSection(
                    icon = Icons.Outlined.ViewList,
                    iconBg = Color(0xFF2EAB6C),
                    title = "Pages",
                    subtitle = "Manage pages and their names"
                ) {
                    if (sections.isEmpty()) {
                        Text(
                            "No pages yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    } else {
                        sections.forEachIndexed { idx, swt ->
                            SectionRow(
                                section = swt.section,
                                onUpdate = { vm.updateSection(it) },
                                onDelete = { vm.deleteSection(it) },
                                onMoveUp = if (idx > 0) {
                                    {
                                        val prev = sections[idx - 1].section
                                        vm.updateSection(swt.section.copy(position = prev.position))
                                        vm.updateSection(prev.copy(position = swt.section.position))
                                    }
                                } else null,
                                onMoveDown = if (idx < sections.lastIndex) {
                                    {
                                        val next = sections[idx + 1].section
                                        vm.updateSection(swt.section.copy(position = next.position))
                                        vm.updateSection(next.copy(position = swt.section.position))
                                    }
                                } else null
                            )
                            if (idx < sections.lastIndex) SettingsDivider()
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    TextButton(
                        onClick = { showAddSection = true },
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp, bottom = 4.dp)
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("Add page")
                    }
                }
            }
            //endregion

            //region Statistics
            item {
                SettingsSection(
                    icon = Icons.Outlined.BarChart,
                    iconBg = Color(0xFF7B61FF),
                    title = "Statistics",
                    subtitle = "Track how often you open links"
                ) {
                    ToggleItem(
                        title = "Track usage",
                        subtitle = "Record link opens with timestamp — off by default",
                        checked = statsEnabled,
                        onToggle = { vm.setStatsEnabled(it) }
                    )
                    SettingsDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable(onClick = onNavigateToStats)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("View statistics", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "Click counts, hourly chart, PDF export",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            //endregion

            //region Backup
            item {
                SettingsSection(
                    icon = Icons.Outlined.Backup,
                    iconBg = Color(0xFF2EAB6C),
                    title = "Backup",
                    subtitle = "Export or import all tiles, pages and settings"
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clickable { exportLauncher.launch("portal_backup.json") }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.FileUpload, contentDescription = null,
                             modifier = Modifier.size(20.dp),
                             tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Export settings", style = MaterialTheme.typography.bodyLarge)
                            Text("Save all tiles, pages and settings to a JSON file",
                                 style = MaterialTheme.typography.bodySmall,
                                 color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    SettingsDivider()
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clickable { importLauncher.launch(arrayOf("application/json", "*/*")) }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.FileDownload, contentDescription = null,
                             modifier = Modifier.size(20.dp),
                             tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Import settings", style = MaterialTheme.typography.bodyLarge)
                            Text("Restore from a previously exported file — replaces current data",
                                 style = MaterialTheme.typography.bodySmall,
                                 color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            //endregion

            //region App lock
            item {
                SettingsSection(
                    icon = if (lockEnabled) Icons.Outlined.Lock else Icons.Outlined.LockOpen,
                    iconBg = Color(0xFF3D4F73),
                    title = "App Lock",
                    subtitle = if (lockEnabled) "Enabled — password required on startup" else "Disabled"
                ) {
                    if (!lockEnabled) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                               verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("Require a password before Portal's home screen becomes visible.",
                                 style = MaterialTheme.typography.bodySmall,
                                 color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("On setup you receive a one-time 16-character reset code — write it down. " +
                                 "It is the only way to regain access if you forget your password. " +
                                 "No biometric unlock, no cloud recovery.",
                                 style = MaterialTheme.typography.bodySmall,
                                 color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("Note: the lock protects Portal's screen only, not the backup file or data " +
                                 "accessible via Android system tools.",
                                 style = MaterialTheme.typography.bodySmall,
                                 color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        SettingsDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable { lockPhase = 1; lockPwInput = ""; lockPwConfirm = ""; lockPwError = "" }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Enable app lock", style = MaterialTheme.typography.bodyLarge)
                            Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null,
                                 modifier = Modifier.size(20.dp),
                                 tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Lock, contentDescription = null,
                                 modifier = Modifier.size(18.dp), tint = Color(0xFF2EAB6C))
                            Text("App lock is active", style = MaterialTheme.typography.bodyMedium,
                                 color = Color(0xFF2EAB6C), fontWeight = FontWeight.SemiBold)
                        }
                        Text(
                            "A password is required on every startup. The password is stored as a " +
                            "salted SHA-256 hash — never in plain text. The lock covers Portal's " +
                            "screen only; use your Android screen lock for device-level protection.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
                        )
                        SettingsDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable { lockPhase = 5; lockPwInput = ""; lockPwError = "" }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Change password", style = MaterialTheme.typography.bodyLarge)
                            Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null,
                                 modifier = Modifier.size(20.dp),
                                 tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        SettingsDivider()
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable { lockPhase = 4; lockPwInput = ""; lockPwError = "" }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Disable app lock",
                                 style = MaterialTheme.typography.bodyLarge,
                                 color = MaterialTheme.colorScheme.error)
                            Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null,
                                 modifier = Modifier.size(20.dp),
                                 tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            //endregion
        }
    }

    //region Dialogs & overlays
    colorPickerTarget?.let { target ->
        val current = when (target) {
            "back"     -> btnColorBack
            "settings" -> btnColorSettings
            "add"      -> btnColorAdd
            "info"     -> btnColorInfo
            else       -> btnColorForward
        }
        val label = when (target) {
            "back"     -> "Back arrow"
            "settings" -> "Settings"
            "add"      -> "Add / Done"
            "info"     -> "Info"
            else       -> "Forward arrow"
        }
        ColorPickerDialog(
            title   = label,
            current = current,
            onSelect = { color ->
                when (target) {
                    "back"     -> vm.setBtnColorBack(color)
                    "settings" -> vm.setBtnColorSettings(color)
                    "add"      -> vm.setBtnColorAdd(color)
                    "info"     -> vm.setBtnColorInfo(color)
                    else       -> vm.setBtnColorForward(color)
                }
            },
            onDismiss = { colorPickerTarget = null }
        )
    }

    iconPickerTarget?.let { target ->
        IconPickerSheet(
            onSelect = { icon ->
                when (target) {
                    "back"     -> vm.setBtnIconBack(icon)
                    "settings" -> vm.setBtnIconSettings(icon)
                    "add"      -> vm.setBtnIconAdd(icon)
                    "info"     -> vm.setBtnIconInfo(icon)
                    else       -> vm.setBtnIconForward(icon)
                }
                iconPickerTarget = null
            },
            onGallery = { iconPickerTarget = null },
            onDismiss = { iconPickerTarget = null }
        )
    }

    if (showAddSection) {
        AlertDialog(
            onDismissRequest = { showAddSection = false; addSectionName = "" },
            title = { Text("New page") },
            text = {
                OutlinedTextField(
                    value = addSectionName,
                    onValueChange = { addSectionName = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (addSectionName.isNotBlank()) vm.addSection(addSectionName)
                    showAddSection = false; addSectionName = ""
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddSection = false; addSectionName = "" }) { Text("Cancel") }
            }
        )
    }

    if (backupMessage.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { backupMessage = "" },
            title = { Text("Backup") },
            text = { Text(backupMessage) },
            confirmButton = { TextButton(onClick = { backupMessage = "" }) { Text("OK") } }
        )
    }

    if (lockPhase == 1) {
        AlertDialog(
            onDismissRequest = { resetLockDialog() },
            title = { Text("Set a password") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Choose a password for Portal. You will receive a one-time reset code on the next screen — write it down.",
                         style = MaterialTheme.typography.bodySmall,
                         color = MaterialTheme.colorScheme.onSurfaceVariant)
                    LockPasswordField("Password", lockPwInput, lockPwVisible, lockPwError,
                        onChange = { lockPwInput = it; lockPwError = "" },
                        onToggleVisible = { lockPwVisible = !lockPwVisible })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (lockPwInput.length < 4) { lockPwError = "At least 4 characters required" }
                    else { lockPwConfirm = ""; lockPhase = 2 }
                }) { Text("Next") }
            },
            dismissButton = { TextButton(onClick = { resetLockDialog() }) { Text("Cancel") } }
        )
    }

    if (lockPhase == 2) {
        AlertDialog(
            onDismissRequest = { resetLockDialog() },
            title = { Text("Confirm password") },
            text = {
                LockPasswordField("Confirm password", lockPwConfirm, lockPwVisible, lockPwError,
                    onChange = { lockPwConfirm = it; lockPwError = "" },
                    onToggleVisible = { lockPwVisible = !lockPwVisible })
            },
            confirmButton = {
                TextButton(onClick = {
                    if (lockPwConfirm != lockPwInput) { lockPwError = "Passwords do not match" }
                    else {
                        onLockEnabled() // mark verified before DataStore update fires
                        vm.setupLock(lockPwInput) { code -> lockResetCode = code; lockPhase = 3 }
                    }
                }) { Text("Enable") }
            },
            dismissButton = { TextButton(onClick = { lockPhase = 1; lockPwConfirm = ""; lockPwError = "" }) { Text("Back") } }
        )
    }

    if (lockPhase == 3 || lockPhase == 7) {
        AlertDialog(
            onDismissRequest = { /* force acknowledge */ },
            title = { Text("Write down your reset code") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("This is the only way to recover access if you forget your password. " +
                         "Write it down now — it will not be shown again.",
                         style = MaterialTheme.typography.bodySmall,
                         color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(lockResetCode,
                         style = MaterialTheme.typography.headlineSmall,
                         fontWeight = FontWeight.Bold,
                         color = MaterialTheme.colorScheme.primary,
                         modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = { resetLockDialog() }) { Text("I've written it down") }
            }
        )
    }

    if (lockPhase == 4) {
        AlertDialog(
            onDismissRequest = { resetLockDialog() },
            title = { Text("Disable app lock") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter your current password to disable the lock.",
                         style = MaterialTheme.typography.bodySmall,
                         color = MaterialTheme.colorScheme.onSurfaceVariant)
                    LockPasswordField("Current password", lockPwInput, lockPwVisible, lockPwError,
                        onChange = { lockPwInput = it; lockPwError = "" },
                        onToggleVisible = { lockPwVisible = !lockPwVisible })
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (vm.verifyPassword(lockPwInput)) { vm.disableLock(); resetLockDialog() }
                        else lockPwError = "Wrong password"
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Disable") }
            },
            dismissButton = { TextButton(onClick = { resetLockDialog() }) { Text("Cancel") } }
        )
    }

    if (lockPhase == 5) {
        AlertDialog(
            onDismissRequest = { resetLockDialog() },
            title = { Text("Change password") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter your current password.",
                         style = MaterialTheme.typography.bodySmall,
                         color = MaterialTheme.colorScheme.onSurfaceVariant)
                    LockPasswordField("Current password", lockPwInput, lockPwVisible, lockPwError,
                        onChange = { lockPwInput = it; lockPwError = "" },
                        onToggleVisible = { lockPwVisible = !lockPwVisible })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (vm.verifyPassword(lockPwInput)) { lockPwInput = ""; lockPwError = ""; lockPhase = 6 }
                    else lockPwError = "Wrong password"
                }) { Text("Next") }
            },
            dismissButton = { TextButton(onClick = { resetLockDialog() }) { Text("Cancel") } }
        )
    }

    if (lockPhase == 6) {
        AlertDialog(
            onDismissRequest = { resetLockDialog() },
            title = { Text("New password") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LockPasswordField("New password", lockPwInput, lockPwVisible, lockPwError,
                        onChange = { lockPwInput = it; lockPwError = "" },
                        onToggleVisible = { lockPwVisible = !lockPwVisible })
                    LockPasswordField("Confirm new password", lockPwConfirm, lockPwVisible, "",
                        onChange = { lockPwConfirm = it },
                        onToggleVisible = { lockPwVisible = !lockPwVisible })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    when {
                        lockPwInput.length < 4 -> lockPwError = "At least 4 characters required"
                        lockPwConfirm != lockPwInput -> lockPwError = "Passwords do not match"
                        else -> vm.changeLockPassword(lockPwInput) { code -> lockResetCode = code; lockPhase = 7 }
                    }
                }) { Text("Change") }
            },
            dismissButton = { TextButton(onClick = { resetLockDialog() }) { Text("Cancel") } }
        )
    }
    //endregion
}

@Composable
private fun LockPasswordField(
    label: String,
    value: String,
    visible: Boolean,
    error: String,
    onChange: (String) -> Unit,
    onToggleVisible: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        isError = error.isNotEmpty(),
        supportingText = if (error.isNotEmpty()) {
            { Text(error, color = MaterialTheme.colorScheme.error) }
        } else null,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onToggleVisible) {
                Icon(
                    if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = null
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SettingsSection(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall,
                     color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }

        // Content card — 6 dp gap only while visible (spacer inside AnimatedVisibility avoids
        // extra space when collapsed, since AnimatedVisibility clips to 0 height on exit)
        AnimatedVisibility(visible = expanded) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun BrowserOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        if (selected) {
            Icon(Icons.Outlined.Check, contentDescription = null,
                 tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun ColorRow(
    icon: ImageVector,
    label: String,
    color: Color,
    iconAsset: String = "",
    onPickIcon: (() -> Unit)? = null,
    onClearIcon: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
            .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp),
                 tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(label, style = MaterialTheme.typography.bodyLarge)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            if (onPickIcon != null) {
                if (iconAsset.isNotEmpty()) {
                    AsyncImage(
                        model = "file:///android_asset/$iconAsset",
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    IconButton(onClick = { onClearIcon?.invoke() }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Outlined.Close, contentDescription = "Remove icon",
                             modifier = Modifier.size(14.dp),
                             tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    IconButton(onClick = onPickIcon, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Outlined.Image, contentDescription = "Pick icon",
                             modifier = Modifier.size(16.dp),
                             tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Box(
                modifier = Modifier.size(24.dp).clip(CircleShape).background(color)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            listOf(
                                Color(0xFFFF0000), Color(0xFFFF8800), Color(0xFFFFFF00),
                                Color(0xFF00CC00), Color(0xFF00CCFF), Color(0xFF0000FF),
                                Color(0xFF9900FF), Color(0xFFFF0000)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun ColorPickerDialog(
    title: String,
    current: Color,
    onSelect: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    var selected by remember(current) { mutableStateOf(current) }
    var hexInput by remember(current) { mutableStateOf(current.toHexString()) }
    var showHsvPicker by remember { mutableStateOf(false) }

    val presets = listOf(
        Color(0xFFFFFF00), Color(0xFFFF0000), Color(0xFF00FF00),
        Color(0xFF0000FF), Color(0xFFFFFFFF), Color(0xFF000000)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                        .clip(RoundedCornerShape(10.dp)).background(selected)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                )
                presets.chunked(3).forEach { rowColors ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowColors.forEach { c ->
                            val isSelected = selected == c
                            Box(
                                modifier = Modifier
                                    .weight(1f).aspectRatio(1f)
                                    .clip(CircleShape).background(c)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.outline,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        selected = c
                                        hexInput = c.toHexString()
                                    }
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showHsvPicker = !showHsvPicker }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(
                                        Color(0xFFFF0000), Color(0xFFFF8800), Color(0xFFFFFF00),
                                        Color(0xFF00CC00), Color(0xFF00CCFF), Color(0xFF0000FF),
                                        Color(0xFF9900FF), Color(0xFFFF0000)
                                    )
                                )
                            )
                    )
                    Text(
                        if (showHsvPicker) "Hide color wheel" else "Custom color",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                AnimatedVisibility(visible = showHsvPicker) {
                    HsvColorPicker(
                        hexColor = hexInput,
                        onHexChanged = { newHex ->
                            hexInput = newHex
                            newHex.hexToColor()?.let { selected = it }
                        }
                    )
                }
                OutlinedTextField(
                    value = hexInput,
                    onValueChange = { raw ->
                        val cleaned = raw.trimStart('#').uppercase().filter { it.isLetterOrDigit() }.take(6)
                        hexInput = cleaned
                        if (cleaned.length == 6) {
                            cleaned.hexToColor()?.let { selected = it }
                        }
                    },
                    label = { Text("Hex") },
                    prefix = { Text("#") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSelect(selected); onDismiss() }) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeItem(selected: ThemePreference, onSelect: (ThemePreference) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp)) {
        Text("Theme", style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(10.dp))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            listOf(ThemePreference.SYSTEM to "System", ThemePreference.LIGHT to "Light", ThemePreference.DARK to "Dark")
                .forEachIndexed { idx, (pref, label) ->
                    SegmentedButton(
                        selected = selected == pref,
                        onClick = { onSelect(pref) },
                        shape = SegmentedButtonDefaults.itemShape(idx, 3),
                        label = { Text(label) }
                    )
                }
        }
    }
}

@Composable
private fun SliderItem(
    title: String,
    valueLabel: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(valueLabel, style = MaterialTheme.typography.bodyMedium,
                 color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
        }
        Slider(value = value, onValueChange = onValueChange, valueRange = range, steps = steps,
               modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun ToggleItem(title: String, subtitle: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall,
                 color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onToggle)
    }
}

@Composable
private fun SectionRow(
    section: Section,
    onUpdate: (Section) -> Unit,
    onDelete: (Section) -> Unit,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null
) {
    var showRename by remember(section.id) { mutableStateOf(false) }
    var nameInput  by remember(section.id) { mutableStateOf(section.name) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable { showRename = true }
                .padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = section.name.ifBlank { "Default" }, style = MaterialTheme.typography.bodyLarge)
                Text("Tap to rename", style = MaterialTheme.typography.bodySmall,
                     color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (onMoveUp != null) {
                IconButton(onClick = onMoveUp) {
                    Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = "Move up",
                         tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (onMoveDown != null) {
                IconButton(onClick = onMoveDown) {
                    Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = "Move down",
                         tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = { onDelete(section) }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
            Text("Grid preset", style = MaterialTheme.typography.bodySmall,
                 color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                listOf(0 to "10×18", 1 to "12×24").forEachIndexed { idx, (preset, label) ->
                    SegmentedButton(
                        selected = section.gridPreset == preset,
                        onClick  = { onUpdate(section.copy(gridPreset = preset)) },
                        shape    = SegmentedButtonDefaults.itemShape(idx, 2),
                        label    = { Text(label) }
                    )
                }
            }
        }

    }

    if (showRename) {
        AlertDialog(
            onDismissRequest = { showRename = false; nameInput = section.name },
            title = { Text("Rename page") },
            text = {
                OutlinedTextField(
                    value = nameInput, onValueChange = { nameInput = it },
                    label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = { onUpdate(section.copy(name = nameInput)); showRename = false }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showRename = false; nameInput = section.name }) { Text("Cancel") }
            }
        )
    }

}
