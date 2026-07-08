package com.bisc.portal.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bisc.portal.data.model.Tile
import com.bisc.portal.util.InvertColorMatrix
import com.bisc.portal.util.domainToColor
import com.bisc.portal.util.extractDomain
import com.bisc.portal.util.hexToColor
import java.io.File

@Composable
fun TileCard(
    tile: Tile,
    globalAutoInvertIcons: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    val bgColor = remember(tile.iconBgEnabled, tile.iconBgColor) {
        if (tile.iconBgEnabled && tile.iconBgColor.isNotEmpty())
            tile.iconBgColor.hexToColor() else null
    }

    val shouldInvert = tile.invertIcon || ((tile.autoInvertIcon || globalAutoInvertIcons) && isDark)
    val iconColorFilter = if (shouldInvert) ColorFilter.colorMatrix(InvertColorMatrix) else null

    Box(modifier = modifier) {
        if (bgColor != null) {
            Box(Modifier.fillMaxSize().background(bgColor))
        }

        Box(modifier = Modifier.fillMaxSize()) {
            val isTextTile = tile.isTextTile && tile.label.isNotEmpty()
            when {
                isTextTile -> {
                    val labelTextColor = remember(tile.labelColor) {
                        tile.labelColor.hexToColor() ?: Color.White
                    }
                    val textAlign = when (tile.labelAlign) {
                        1    -> TextAlign.Center
                        2    -> TextAlign.End
                        else -> TextAlign.Start
                    }
                    val fontWeight = if (tile.labelBold) FontWeight.Bold else FontWeight.Normal
                    val fontStyle = if (tile.labelItalic) FontStyle.Italic else FontStyle.Normal
                    if (tile.labelFontSize > 0) {
                        Text(
                            text = tile.label,
                            color = labelTextColor,
                            fontSize = tile.labelFontSize.sp,
                            textAlign = textAlign,
                            fontWeight = fontWeight,
                            fontStyle = fontStyle,
                            overflow = TextOverflow.Clip,
                            modifier = Modifier.fillMaxSize().padding(8.dp)
                        )
                    } else {
                        AutoFitText(
                            text = tile.label,
                            tileId = tile.id,
                            color = labelTextColor,
                            textAlign = textAlign,
                            fontWeight = fontWeight,
                            fontStyle = fontStyle,
                            modifier = Modifier.fillMaxSize().padding(8.dp)
                        )
                    }
                }
                tile.iconAsset.isNotEmpty() || tile.iconUri.isNotEmpty() ->
                    IconOnlyContent(tile, iconColorFilter, Modifier.fillMaxSize(), tile.iconZoom)
                else -> FallbackTileContent(tile = tile)
            }
        }

        if (tile.showLabel && tile.label.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = tile.label,
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AutoFitText(
    text: String,
    tileId: Long,
    color: Color,
    textAlign: TextAlign,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle = FontStyle.Normal,
    modifier: Modifier
) {
    var fontSize by remember(tileId, text) { mutableStateOf(200.sp) }
    var readyToDraw by remember(tileId, text) { mutableStateOf(false) }
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        textAlign = textAlign,
        fontWeight = fontWeight,
        fontStyle = fontStyle,
        overflow = TextOverflow.Clip,
        softWrap = true,
        modifier = modifier.drawWithContent { if (readyToDraw) drawContent() },
        onTextLayout = { result ->
            if ((result.didOverflowHeight || result.didOverflowWidth) && fontSize.value > 8f) {
                fontSize = (fontSize.value * 0.85f).sp
            } else {
                readyToDraw = true
            }
        }
    )
}

@Composable
fun IconOnlyContent(tile: Tile, colorFilter: ColorFilter?, modifier: Modifier = Modifier, zoom: Float = 1f) {
    val context = LocalContext.current
    val scaledModifier = if (zoom != 1f) modifier.scale(zoom) else modifier
    when {
        tile.iconAsset.isNotEmpty() && !tile.iconAsset.contains("/") -> {
            val resId = remember(tile.iconAsset) {
                context.resources.getIdentifier(tile.iconAsset, "drawable", context.packageName)
            }
            if (resId != 0) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(resId).crossfade(true).build(),
                    contentDescription = tile.label.ifBlank { tile.url },
                    contentScale = ContentScale.Crop,
                    colorFilter = colorFilter,
                    modifier = scaledModifier
                )
            } else {
                FallbackTileContent(tile)
            }
        }
        tile.iconAsset.isNotEmpty() -> AsyncImage(
            model = ImageRequest.Builder(context)
                .data("file:///android_asset/${tile.iconAsset}")
                .crossfade(true)
                .build(),
            contentDescription = tile.label.ifBlank { tile.url },
            contentScale = ContentScale.Crop,
            colorFilter = colorFilter,
            modifier = scaledModifier
        )
        tile.iconUri.isNotEmpty() -> AsyncImage(
            model = ImageRequest.Builder(context)
                .data(File(tile.iconUri))
                .crossfade(true)
                .build(),
            contentDescription = tile.label.ifBlank { tile.url },
            contentScale = ContentScale.Crop,
            colorFilter = colorFilter,
            modifier = scaledModifier
        )
        else -> FallbackTileContent(tile)
    }
}

@Composable
fun FallbackTileContent(tile: Tile) {
    val domain = extractDomain(tile.url)
    val bg = domainToColor(domain)
    val letter = tile.label.firstOrNull()?.uppercaseChar()?.toString()
        ?: domain.firstOrNull()?.uppercaseChar()?.toString()
        ?: "?"
    Box(
        modifier = Modifier.fillMaxSize().background(bg),
        contentAlignment = Alignment.Center
    ) {
        Text(letter, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
    }
}
