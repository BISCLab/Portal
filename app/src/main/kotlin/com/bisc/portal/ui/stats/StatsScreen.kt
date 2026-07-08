package com.bisc.portal.ui.stats

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val dateFmt = SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault())
private val pdfDateFmt = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    vm: StatsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val stats by vm.stats.collectAsState()
    val statsEnabled by vm.statsEnabled.collectAsState()
    val context = LocalContext.current

    var showClearDialog by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        message?.let { snackbarHost.showSnackbar(it); message = null }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHost) },
        topBar = {
            TopAppBar(
                title = { Text("Statistics", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Track usage", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                "Records every link you open with a timestamp",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(checked = statsEnabled, onCheckedChange = { vm.setStatsEnabled(it) })
                    }
                }
            }

            if (stats.totalClicks > 0) {

                item {
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                "${stats.totalClicks} total clicks",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "${stats.tiles.size} different links",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "By hour of day",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(10.dp))
                            val barColor = MaterialTheme.colorScheme.primary
                            val maxCount = stats.hourlyGlobal.max().coerceAtLeast(1)
                            Canvas(modifier = Modifier.fillMaxWidth().height(64.dp)) {
                                val barW = size.width / 24f
                                stats.hourlyGlobal.forEachIndexed { hour, count ->
                                    if (count > 0) {
                                        val barH = (count.toFloat() / maxCount) * size.height
                                        drawRect(
                                            color = barColor,
                                            topLeft = Offset(hour * barW + 1f, size.height - barH),
                                            size = Size(barW - 2f, barH)
                                        )
                                    }
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "0h",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    "12h",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    "23h",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                item {
                    Text(
                        "Links",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                items(stats.tiles, key = { it.tileId }) { entry ->
                    TileStatCard(entry)
                }

                item {
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    val uri = generateStatsPdf(context, stats)
                                    message = if (uri != null) "PDF saved to Downloads" else "Export failed"
                                } else {
                                    message = "Requires Android 10 or later"
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Export PDF")
                        }
                        OutlinedButton(
                            onClick = { showClearDialog = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Clear all")
                        }
                    }
                }

            } else {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "No data yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (!statsEnabled) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Enable tracking above to start recording link opens",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear statistics") },
            text = { Text("Delete all recorded click history? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = { vm.clearStats(); showClearDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Clear") }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun TileStatCard(entry: TileStatEntry) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = entry.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Last: ${dateFmt.format(Date(entry.lastClicked))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "${entry.count}×",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@SuppressLint("NewApi")
private fun generateStatsPdf(context: Context, stats: PortalStats): Uri? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return null

    val pdfDoc = PdfDocument()
    val page = pdfDoc.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
    val canvas = page.canvas

    val titlePaint  = Paint().apply { textSize = 20f; isFakeBoldText = true; color = android.graphics.Color.BLACK }
    val headPaint   = Paint().apply { textSize = 13f; isFakeBoldText = true; color = android.graphics.Color.BLACK }
    val bodyPaint   = Paint().apply { textSize = 10f; color = android.graphics.Color.BLACK }
    val grayPaint   = Paint().apply { textSize = 10f; color = android.graphics.Color.DKGRAY }
    val linePaint   = Paint().apply { color = android.graphics.Color.LTGRAY; strokeWidth = 0.5f; style = Paint.Style.STROKE }
    val accentPaint = Paint().apply { textSize = 10f; isFakeBoldText = true; color = android.graphics.Color.parseColor("#000B29") }

    val m = 44f          // margin
    val w = 595f - m * 2 // content width
    var y = 56f

    canvas.drawText("Portal — Statistics", m, y, titlePaint); y += 18f
    canvas.drawText("Generated: ${pdfDateFmt.format(Date())}", m, y, grayPaint); y += 12f
    canvas.drawText("Total clicks: ${stats.totalClicks}  ·  ${stats.tiles.size} links", m, y, grayPaint); y += 20f
    canvas.drawLine(m, y, m + w, y, linePaint); y += 14f

    canvas.drawText("LINKS (sorted by clicks)", m, y, headPaint); y += 14f
    canvas.drawLine(m, y, m + w, y, linePaint); y += 12f

    stats.tiles.take(28).forEach { entry ->
        if (y > 660f) return@forEach
        val label = entry.label.take(34).let { if (entry.label.length > 34) "$it…" else it }
        val url   = entry.url.take(44).let { if (entry.url.length > 44) "$it…" else it }
        canvas.drawText("${entry.count}×", m, y, accentPaint)
        canvas.drawText(label, m + 30f, y, bodyPaint)
        canvas.drawText(url, m + 30f, y + 11f, grayPaint)
        canvas.drawText("Last: ${pdfDateFmt.format(Date(entry.lastClicked))}", m + 30f, y + 22f, grayPaint)
        y += 32f
    }

    y += 8f
    canvas.drawLine(m, y, m + w, y, linePaint); y += 14f
    canvas.drawText("CLICKS BY HOUR OF DAY", m, y, headPaint); y += 14f

    val colW = w / 4f
    stats.hourlyGlobal.forEachIndexed { hour, count ->
        val col = hour % 4
        val row = hour / 4
        val x = m + col * colW
        val ry = y + row * 14f
        canvas.drawText("${hour.toString().padStart(2, '0')}:00  $count", x, ry, bodyPaint)
    }

    pdfDoc.finishPage(page)

    // Write to Downloads via MediaStore (no permission needed on API 29+)
    val values = ContentValues().apply {
        put(MediaStore.Downloads.DISPLAY_NAME, "portal_stats_${System.currentTimeMillis()}.pdf")
        put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
        put(MediaStore.Downloads.IS_PENDING, 1)
    }
    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values) ?: run {
        pdfDoc.close(); return null
    }
    return try {
        resolver.openOutputStream(uri)?.use { pdfDoc.writeTo(it) }
        values.clear(); values.put(MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        uri
    } catch (e: Exception) {
        resolver.delete(uri, null, null)
        null
    } finally {
        pdfDoc.close()
    }
}
