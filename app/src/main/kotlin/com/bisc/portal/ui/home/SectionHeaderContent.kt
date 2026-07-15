package com.bisc.portal.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bisc.portal.data.model.Section

@Composable
internal fun SectionHeaderContent(section: Section, unitPx: Float) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val mode = section.sectionColumns

    fun resolveIconModel(): Any? {
        if (section.headerIcon.isBlank()) return null
        return "file:///android_asset/${section.headerIcon}"
    }

    when (mode) {
        1 -> {
            val model = resolveIconModel()
            if (model != null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(model).build(),
                        contentDescription = null,
                        modifier = Modifier.size(with(density) { (unitPx * 0.65f).toDp() }),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
            }
        }
        3 -> {
            Box(
                modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = section.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        else -> {
            // Mode 2: icon + text
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val model = resolveIconModel()
                if (model != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(model).build(),
                        contentDescription = null,
                        modifier = Modifier.size(with(density) { (unitPx * 0.55f).toDp() }),
                        contentScale = ContentScale.Fit,
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
                Text(
                    text = section.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
