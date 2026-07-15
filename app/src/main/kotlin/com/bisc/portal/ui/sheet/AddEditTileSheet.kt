package com.bisc.portal.ui.sheet

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bisc.portal.data.model.Section
import com.bisc.portal.data.model.Tile
import com.bisc.portal.util.InvertColorMatrix
import com.bisc.portal.util.hexToColor
import com.bisc.portal.util.normalizeUrl
import java.io.File
import java.util.UUID

private val BG_PRESETS = listOf("FFFF00", "FF0000", "00FF00", "0000FF", "FFFFFF", "000000")
private val TEXT_COLOR_PRESETS = listOf("FFFFFF", "000B29", "F34A4A", "FFD700", "000000")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTileSheet(
    initial: Tile? = null,
    defaultSectionId: Long,
    allSections: List<Section>,
    onSave: (Tile) -> Unit,
    onDelete: (() -> Unit)? = null,
    onMove: ((Long) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isEdit = initial != null

    var url        by remember { mutableStateOf(initial?.url ?: "") }
    var label      by remember { mutableStateOf(initial?.label ?: "") }
    var showLabel  by remember { mutableStateOf(initial?.showLabel ?: false) }
    var iconAsset  by remember { mutableStateOf(initial?.iconAsset ?: "") }
    var iconUri    by remember { mutableStateOf(initial?.iconUri ?: "") }
    var sectionId  by remember { mutableStateOf(initial?.sectionId ?: defaultSectionId) }

    var iconBgEnabled by remember { mutableStateOf(initial?.iconBgEnabled ?: false) }
    var iconBgColor   by remember { mutableStateOf(initial?.iconBgColor?.ifEmpty { "F8F5F2" } ?: "F8F5F2") }
    var invertIcon    by remember { mutableStateOf(initial?.invertIcon ?: false) }

    // Tile mode: 0=icon, 1=text (Icon+Text removed)
    var tileMode by remember {
        mutableStateOf(if (initial?.isTextTile == true) 1 else 0)
    }

    var labelColor    by remember { mutableStateOf(initial?.labelColor?.ifEmpty { "FFFFFF" } ?: "FFFFFF") }
    var labelAlign    by remember { mutableStateOf(initial?.labelAlign ?: 0) }
    var labelFontSize by remember { mutableStateOf(initial?.labelFontSize ?: 0) }
    var sizeInput     by remember {
        mutableStateOf(if ((initial?.labelFontSize ?: 0) > 0) (initial?.labelFontSize ?: 0).toString() else "")
    }
    var labelBold     by remember { mutableStateOf(initial?.labelBold ?: false) }
    var labelItalic   by remember { mutableStateOf(initial?.labelItalic ?: false) }
    var iconZoom      by remember { mutableStateOf(initial?.iconZoom ?: 1f) }

    var showIconPicker        by remember { mutableStateOf(false) }
    var showColorPicker       by remember { mutableStateOf(false) }
    var showTextColorPicker   by remember { mutableStateOf(false) }
    var sectionExpanded       by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val dest = File(context.filesDir, "tile_icons/${UUID.randomUUID()}.png")
            dest.parentFile?.mkdirs()
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { it.copyTo(dest.outputStream()) }
                iconUri = dest.absolutePath
                iconAsset = ""
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            //region Mode toggle, URL, label
            Text(
                if (isEdit) "Edit tile" else "Add tile",
                style = MaterialTheme.typography.titleMedium
            )

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                listOf(0 to "Icon", 1 to "Text").forEachIndexed { idx, (mode, label2) ->
                    SegmentedButton(
                        selected = tileMode == mode,
                        onClick = { tileMode = mode },
                        shape = SegmentedButtonDefaults.itemShape(idx, 2),
                        label = { Text(label2) }
                    )
                }
            }

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL (optional)") },
                placeholder = { Text("https://") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Label — in text mode shows live tile preview (background + text color)
            if (tileMode == 1) {
                val previewBg = if (iconBgEnabled)
                    (iconBgColor.hexToColor() ?: MaterialTheme.colorScheme.surfaceVariant)
                else
                    MaterialTheme.colorScheme.surfaceVariant
                val previewFg = labelColor.hexToColor() ?: Color.White
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Label") },
                    minLines = 2,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(
                        color = previewFg,
                        fontWeight = if (labelBold) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if (labelItalic) FontStyle.Italic else FontStyle.Normal,
                        textAlign = when (labelAlign) {
                            1    -> TextAlign.Center
                            2    -> TextAlign.End
                            else -> TextAlign.Start
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = previewBg,
                        unfocusedContainerColor = previewBg,
                        focusedTextColor = previewFg,
                        unfocusedTextColor = previewFg,
                    )
                )
            } else {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Label (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            //endregion

            //region Icon preview & picker trigger
            if (tileMode == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show label", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = showLabel, onCheckedChange = { showLabel = it })
                }
            }

            if (tileMode != 1) {
                val previewColorFilter = if (invertIcon) ColorFilter.colorMatrix(InvertColorMatrix) else null
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (iconBgEnabled) iconBgColor.hexToColor() ?: MaterialTheme.colorScheme.surfaceVariant
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            .clickable { showIconPicker = true }
                    ) {
                        val previewModel = remember(iconAsset, iconUri) {
                            when {
                                iconAsset.isNotEmpty() ->
                                    ImageRequest.Builder(context).data("file:///android_asset/$iconAsset").crossfade(true).build()
                                iconUri.isNotEmpty() ->
                                    ImageRequest.Builder(context).data(File(iconUri)).crossfade(true).build()
                                else -> null
                            }
                        }
                        if (previewModel != null) {
                            AsyncImage(
                                model = previewModel,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                colorFilter = previewColorFilter,
                                modifier = Modifier.fillMaxWidth().aspectRatio(1f).scale(iconZoom)
                            )
                        }
                    }
                    TextButton(onClick = { showIconPicker = true }) { Text("Choose icon") }
                }
            }

            //endregion

            //region Icon appearance (background color, invert, zoom)
            HorizontalDivider()
            Text(
                if (tileMode == 1) "Tile appearance" else "Icon appearance",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Background color", style = MaterialTheme.typography.bodyMedium)
                Switch(checked = iconBgEnabled, onCheckedChange = { iconBgEnabled = it })
            }

            if (iconBgEnabled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BG_PRESETS.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(hex.hexToColor() ?: Color.White)
                                .border(
                                    width = if (iconBgColor.equals(hex, ignoreCase = true)) 2.dp else 0.5.dp,
                                    color = if (iconBgColor.equals(hex, ignoreCase = true))
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                                .clickable { iconBgColor = hex }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(26.dp)
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
                            .clickable { showColorPicker = true }
                    )
                }

                if (showColorPicker) {
                    Spacer(Modifier.height(4.dp))
                    HsvColorPicker(
                        hexColor = iconBgColor,
                        onHexChanged = { iconBgColor = it }
                    )
                    Spacer(Modifier.height(4.dp))
                }

                OutlinedTextField(
                    value = "#$iconBgColor",
                    onValueChange = { v ->
                        val stripped = v.trimStart('#').uppercase()
                        if (stripped.length <= 6) iconBgColor = stripped
                    },
                    label = { Text("Hex") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (tileMode != 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Invert icon colors", style = MaterialTheme.typography.bodyMedium)
                        Text("Inverts all colors — makes black icons appear white",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = invertIcon, onCheckedChange = { invertIcon = it })
                }

                if (iconAsset.isNotEmpty() || iconUri.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Icon zoom", style = MaterialTheme.typography.bodyMedium)
                        Text("×${"%.1f".format(iconZoom)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Slider(
                        value = iconZoom,
                        onValueChange = { iconZoom = it },
                        valueRange = 0.5f..3f,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            //endregion

            //region Text appearance (color, alignment, size, style)
            if (tileMode != 0) {
                HorizontalDivider()
                Text(
                    "Text",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TEXT_COLOR_PRESETS.forEach { hex ->
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(hex.hexToColor() ?: Color.White)
                                .border(
                                    width = if (labelColor.equals(hex, ignoreCase = true)) 2.dp else 0.5.dp,
                                    color = if (labelColor.equals(hex, ignoreCase = true))
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                )
                                .clickable { labelColor = hex }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(26.dp)
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
                            .clickable { showTextColorPicker = !showTextColorPicker }
                    )
                }

                if (showTextColorPicker) {
                    Spacer(Modifier.height(4.dp))
                    HsvColorPicker(
                        hexColor = labelColor,
                        onHexChanged = { labelColor = it }
                    )
                    Spacer(Modifier.height(4.dp))
                }

                OutlinedTextField(
                    value = "#$labelColor",
                    onValueChange = { v ->
                        val stripped = v.trimStart('#').uppercase()
                        if (stripped.length <= 6) labelColor = stripped
                    },
                    label = { Text("Text color hex") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Alignment", style = MaterialTheme.typography.bodyMedium)
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    listOf(0 to "Left", 1 to "Center", 2 to "Right").forEachIndexed { idx, (align, name) ->
                        SegmentedButton(
                            selected = labelAlign == align,
                            onClick = { labelAlign = align },
                            shape = SegmentedButtonDefaults.itemShape(idx, 3),
                            label = { Text(name) }
                        )
                    }
                }

                Text("Font size", style = MaterialTheme.typography.bodyMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = labelFontSize == 0,
                        onClick = { labelFontSize = 0; sizeInput = "" },
                        label = { Text("Auto") }
                    )
                    Box(
                        modifier = Modifier
                            .height(32.dp)
                            .widthIn(min = 64.dp)
                            .clip(CircleShape)
                            .background(
                                if (labelFontSize != 0) MaterialTheme.colorScheme.secondaryContainer
                                else Color.Transparent
                            )
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicTextField(
                            value = sizeInput,
                            onValueChange = { v ->
                                val digits = v.filter { it.isDigit() }.take(3)
                                sizeInput = digits
                                labelFontSize = digits.toIntOrNull()?.takeIf { it in 1..999 } ?: 0
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = MaterialTheme.typography.labelLarge.copy(
                                color = if (labelFontSize != 0) MaterialTheme.colorScheme.onSecondaryContainer
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            ),
                            decorationBox = { innerTextField ->
                                Box(contentAlignment = Alignment.Center) {
                                    if (sizeInput.isEmpty()) {
                                        Text("sp", style = MaterialTheme.typography.labelLarge,
                                             color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }

                Text("Style", style = MaterialTheme.typography.bodyMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = labelBold,
                        onClick = { labelBold = !labelBold },
                        label = { Text("B", fontWeight = FontWeight.Bold) }
                    )
                    FilterChip(
                        selected = labelItalic,
                        onClick = { labelItalic = !labelItalic },
                        label = { Text("I", fontStyle = FontStyle.Italic) }
                    )
                }
            }

            //endregion

            //region Section picker
            if (allSections.size > 1) {
                ExposedDropdownMenuBox(
                    expanded = sectionExpanded,
                    onExpandedChange = { sectionExpanded = it }
                ) {
                    OutlinedTextField(
                        value = allSections.firstOrNull { it.id == sectionId }?.name?.ifBlank { "Default" } ?: "Default",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Section") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(sectionExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )
                    ExposedDropdownMenu(
                        expanded = sectionExpanded,
                        onDismissRequest = { sectionExpanded = false }
                    ) {
                        allSections.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s.name.ifBlank { "Default" }) },
                                onClick = {
                                    sectionId = s.id
                                    sectionExpanded = false
                                    onMove?.invoke(s.id)
                                }
                            )
                        }
                    }
                }
            }

            //endregion

            //region Save & delete
            Spacer(Modifier.height(4.dp))

            Button(
                onClick = {
                    onSave(
                        Tile(
                            id        = initial?.id ?: 0L,
                            sectionId = sectionId,
                            label     = label,
                            showLabel = if (tileMode != 0) false else showLabel,
                            url       = normalizeUrl(url),
                            iconAsset = if (tileMode == 1) "" else iconAsset,
                            iconUri   = if (tileMode == 1) "" else iconUri,
                            position  = initial?.position ?: 0,
                            gridCol   = initial?.gridCol ?: 0,
                            gridRow   = initial?.gridRow ?: 0,
                            colSpan   = initial?.colSpan?.takeIf { it > 0 } ?: 2,
                            rowSpan   = initial?.rowSpan?.takeIf { it > 0 } ?: 2,
                            iconShape      = "square",
                            iconScale      = "crop",
                            iconBgColor    = if (iconBgEnabled) iconBgColor else "",
                            iconBgEnabled  = iconBgEnabled,
                            invertIcon       = if (tileMode == 1) false else invertIcon,
                            isTextTile       = tileMode == 1,
                            isIconTextTile   = false,
                            iconTextPosition = 0,
                            labelColor    = if (tileMode != 0) labelColor else (initial?.labelColor ?: "FFFFFF"),
                            labelAlign    = if (tileMode != 0) labelAlign else (initial?.labelAlign ?: 0),
                            labelFontSize = if (tileMode != 0) labelFontSize else (initial?.labelFontSize ?: 0),
                            labelBold     = if (tileMode != 0) labelBold else false,
                            labelItalic   = if (tileMode != 0) labelItalic else false,
                            iconZoom      = if (tileMode != 1) iconZoom else 1f,
                        )
                    )
                },
                enabled = if (tileMode == 1) label.isNotBlank() else (url.isNotBlank() || label.isNotBlank()),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEdit) "Save" else "Add")
            }

            if (onDelete != null) {
                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete tile")
                }
            }
            //endregion
        }
    }

    if (showIconPicker) {
        IconPickerSheet(
            onSelect = { asset -> iconAsset = asset; iconUri = ""; showIconPicker = false },
            onGallery = { showIconPicker = false; galleryLauncher.launch("image/*") },
            onDismiss = { showIconPicker = false }
        )
    }
}

@Composable
fun HsvColorPicker(
    hexColor: String,
    onHexChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val initialColor = hexColor.hexToColor() ?: Color.White
    val hsvArr = FloatArray(3)
    android.graphics.Color.colorToHSV(
        (initialColor.toArgb() or 0xFF000000.toInt()),
        hsvArr
    )

    var hue by remember(hexColor) { mutableStateOf(hsvArr[0] / 360f) }  // 0..1
    var sat by remember(hexColor) { mutableStateOf(hsvArr[1]) }          // 0..1
    var bri by remember(hexColor) { mutableStateOf(hsvArr[2]) }          // 0..1

    fun emit() {
        val argb = android.graphics.Color.HSVToColor(floatArrayOf(hue * 360f, sat, bri))
        onHexChanged(String.format("%06X", argb and 0xFFFFFF))
    }

    val hueColor = Color(android.graphics.Color.HSVToColor(floatArrayOf(hue * 360f, 1f, 1f)))

    Column(modifier = modifier.fillMaxWidth()) {
        Text("Hue", style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        GradientSlider(
            value = hue,
            onValueChange = { hue = it; emit() },
            gradientColors = listOf(
                Color.Red, Color(0xFFFF7F00), Color.Yellow, Color(0xFF00FF00),
                Color.Cyan, Color.Blue, Color(0xFF8B00FF), Color.Red
            )
        )
        Spacer(Modifier.height(4.dp))

        Text("Saturation", style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        GradientSlider(
            value = sat,
            onValueChange = { sat = it; emit() },
            gradientColors = listOf(Color.White, hueColor)
        )
        Spacer(Modifier.height(4.dp))

        Text("Brightness", style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        GradientSlider(
            value = bri,
            onValueChange = { bri = it; emit() },
            gradientColors = listOf(Color.Black, hueColor)
        )
    }
}

@Composable
private fun GradientSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    gradientColors: List<Color>
) {
    Box(modifier = Modifier.fillMaxWidth().height(32.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(5.dp))
                .background(Brush.horizontalGradient(gradientColors))
        )
        // Standard Slider on top for gesture handling — track made transparent
        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor   = Color.Transparent,
                inactiveTrackColor = Color.Transparent,
                activeTickColor    = Color.Transparent,
                inactiveTickColor  = Color.Transparent
            )
        )
    }
}
