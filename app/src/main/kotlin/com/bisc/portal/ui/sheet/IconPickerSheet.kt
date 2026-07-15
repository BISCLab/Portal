package com.bisc.portal.ui.sheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconPickerSheet(
    onSelect: (iconAsset: String) -> Unit,
    onGallery: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var search by remember { mutableStateOf("") }

    val assetIconPaths = remember {
        runCatching {
            (context.assets.list("icons") ?: emptyArray())
                .filter { it.endsWith(".svg") || it.endsWith(".webp") || it.endsWith(".png") }
                .sorted()
                .map { "icons/$it" }
        }.getOrDefault(emptyList())
    }

    val filtered = remember(search, assetIconPaths) {
        val q = search.trim()
        if (q.isBlank()) assetIconPaths
        else assetIconPaths.filter {
            it.substringAfterLast("/").substringBeforeLast(".").contains(q, ignoreCase = true)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Search icons…") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        TextButton(
            onClick = { onDismiss(); onGallery() },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(Icons.Outlined.PhotoLibrary, contentDescription = null)
            Text("  Choose from gallery")
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered, key = { it }) { name ->
                val model = remember(name) {
                    ImageRequest.Builder(context)
                        .data("file:///android_asset/$name")
                        .crossfade(true)
                        .build()
                }
                AsyncImage(
                    model = model,
                    contentDescription = name.substringAfterLast("/").substringBeforeLast("."),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onSelect(name) }
                )
            }
        }
    }
}
