package com.bisc.portal.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.toArgb

val InvertColorMatrix = ColorMatrix(floatArrayOf(
    -1f,  0f,  0f, 0f, 255f,
     0f, -1f,  0f, 0f, 255f,
     0f,  0f, -1f, 0f, 255f,
     0f,  0f,  0f, 1f,   0f
))

fun Color.toHexString(): String = String.format("%06X", toArgb() and 0xFFFFFF)

fun String.hexToColor(): Color? = try {
    Color(android.graphics.Color.parseColor(if (startsWith("#")) this else "#$this"))
} catch (_: Exception) { null }

fun domainToColor(domain: String): Color {
    val hash = domain.fold(0) { acc, c -> acc * 31 + c.code }
    val hue = ((hash and 0x7FFFFFFF) % 360).toFloat()
    return hslToColor(hue, 0.45f, 0.40f)
}

private fun hslToColor(h: Float, s: Float, l: Float): Color {
    val c = (1f - kotlin.math.abs(2f * l - 1f)) * s
    val x = c * (1f - kotlin.math.abs((h / 60f) % 2f - 1f))
    val m = l - c / 2f
    val (r, g, b) = when {
        h < 60f  -> Triple(c, x, 0f)
        h < 120f -> Triple(x, c, 0f)
        h < 180f -> Triple(0f, c, x)
        h < 240f -> Triple(0f, x, c)
        h < 300f -> Triple(x, 0f, c)
        else     -> Triple(c, 0f, x)
    }
    return Color(r + m, g + m, b + m)
}
