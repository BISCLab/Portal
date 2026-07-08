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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

class TinyPortalConfigActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val widgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // Required: set RESULT_CANCELED so the widget is not added if the user backs out
        setResult(RESULT_CANCELED, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            PortalTheme(themePreference = ThemePreference.SYSTEM) {
                TinyPortalConfigScreen(
                    widgetId = widgetId,
                    onSave = { url, iconAsset, iconUri ->
                        saveAndUpdate(widgetId, url, iconAsset, iconUri)
                        setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
                        finish()
                    },
                    onCancel = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }

    private fun saveAndUpdate(widgetId: Int, url: String, iconAsset: String, iconUri: String) {
        getSharedPreferences(TinyPortalWidget.PREFS, Context.MODE_PRIVATE).edit()
            .putString("${TinyPortalWidget.KEY_URL}$widgetId", url)
            .putString("${TinyPortalWidget.KEY_ICON_ASSET}$widgetId", iconAsset)
            .putString("${TinyPortalWidget.KEY_ICON_URI}$widgetId", iconUri)
            .apply()

        TinyPortalWidget.updateWidget(this, AppWidgetManager.getInstance(this), widgetId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TinyPortalConfigScreen(
    widgetId: Int,
    onSave: (url: String, iconAsset: String, iconUri: String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    var url by remember { mutableStateOf("") }
    var iconAsset by remember { mutableStateOf("") }
    var iconUri by remember { mutableStateOf("") }
    var showIconPicker by remember { mutableStateOf(false) }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tiny Portal") },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // URL
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL") },
                placeholder = { Text("https://") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Icon picker row
            Column {
                Text(
                    "Icon",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Preview
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                            .clickable { showIconPicker = true },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            iconAsset.isNotEmpty() -> AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data("file:///android_asset/$iconAsset")
                                    .crossfade(true).build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            iconUri.isNotEmpty() -> AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(File(iconUri)).crossfade(true).build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            else -> Text("?", style = MaterialTheme.typography.headlineMedium,
                                         color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    TextButton(onClick = { showIconPicker = true }) {
                        Text("Choose icon")
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { onSave(normalizeUrl(url), iconAsset, iconUri) },
                enabled = url.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add to home screen")
            }

            Spacer(Modifier.height(8.dp))
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
