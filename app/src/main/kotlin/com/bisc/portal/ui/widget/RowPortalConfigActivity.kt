package com.bisc.portal.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bisc.portal.ui.sheet.IconPickerSheet
import com.bisc.portal.ui.theme.PortalTheme
import com.bisc.portal.ui.theme.ThemePreference
import com.bisc.portal.util.normalizeUrl
import java.io.File
import java.util.UUID

data class SlotData(
    val url: String = "",
    val iconAsset: String = "",
    val iconUri: String = ""
)

class RowPortalConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val widgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        setResult(RESULT_CANCELED, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) { finish(); return }

        setContent {
            PortalTheme(themePreference = ThemePreference.SYSTEM) {
                RowPortalConfigScreen(
                    onSave = { slots ->
                        saveSlots(widgetId, slots)
                        RowPortalWidget.updateWidget(this, AppWidgetManager.getInstance(this), widgetId)
                        setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
                        finish()
                    },
                    onCancel = { setResult(RESULT_CANCELED); finish() }
                )
            }
        }
    }

    private fun saveSlots(widgetId: Int, slots: List<SlotData>) {
        val ed = getSharedPreferences(RowPortalWidget.PREFS, Context.MODE_PRIVATE).edit()
        slots.forEachIndexed { idx, slot ->
            val s = idx + 1
            ed.putString("${RowPortalWidget.KEY_URL}${widgetId}_$s", slot.url)
              .putString("${RowPortalWidget.KEY_ICON_ASSET}${widgetId}_$s", slot.iconAsset)
              .putString("${RowPortalWidget.KEY_ICON_URI}${widgetId}_$s", slot.iconUri)
        }
        ed.apply()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RowPortalConfigScreen(
    onSave: (List<SlotData>) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var slots by remember { mutableStateOf(List(5) { SlotData() }) }
    var iconPickerSlot by remember { mutableStateOf<Int?>(null) }
    var galleryTargetSlot by remember { mutableStateOf<Int?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val slot = galleryTargetSlot ?: return@rememberLauncherForActivityResult
        if (uri != null) {
            val dest = File(context.filesDir, "tile_icons/${UUID.randomUUID()}.png")
            dest.parentFile?.mkdirs()
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { it.copyTo(dest.outputStream()) }
                slots = slots.toMutableList().also { it[slot] = it[slot].copy(iconUri = dest.absolutePath, iconAsset = "") }
            }
        }
        galleryTargetSlot = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Row Portal") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Cancel")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            for (idx in 0 until 5) {
                SlotRow(
                    index = idx,
                    slot = slots[idx],
                    onUrlChange = { url ->
                        slots = slots.toMutableList().also { it[idx] = it[idx].copy(url = url) }
                    },
                    onPickIcon = { iconPickerSlot = idx }
                )
                if (idx < 4) HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    onSave(slots.map { it.copy(url = if (it.url.isNotBlank()) normalizeUrl(it.url) else "") })
                },
                enabled = slots.any { it.url.isNotBlank() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add to home screen")
            }

            Spacer(Modifier.height(8.dp))
        }
    }

    iconPickerSlot?.let { slot ->
        IconPickerSheet(
            onSelect = { asset ->
                slots = slots.toMutableList().also { it[slot] = it[slot].copy(iconAsset = asset, iconUri = "") }
                iconPickerSlot = null
            },
            onGallery = {
                iconPickerSlot = null
                galleryTargetSlot = slot
                galleryLauncher.launch("image/*")
            },
            onDismiss = { iconPickerSlot = null }
        )
    }
}

@Composable
private fun SlotRow(
    index: Int,
    slot: SlotData,
    onUrlChange: (String) -> Unit,
    onPickIcon: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "${index + 1}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(20.dp)
        )

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outline)
                .clickable(onClick = onPickIcon),
            contentAlignment = Alignment.Center
        ) {
            when {
                slot.iconAsset.isNotEmpty() -> AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("file:///android_asset/${slot.iconAsset}").crossfade(true).build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                slot.iconUri.isNotEmpty() -> AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(File(slot.iconUri)).crossfade(true).build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                else -> Text("?", style = MaterialTheme.typography.titleMedium,
                             color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        OutlinedTextField(
            value = slot.url,
            onValueChange = onUrlChange,
            label = { Text("URL") },
            placeholder = { Text("https://") },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
    }
}
