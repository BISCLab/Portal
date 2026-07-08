package com.bisc.portal.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface

internal fun loadAssetBitmap(context: Context, assetPath: String): Bitmap? =
    runCatching { context.assets.open(assetPath).use { BitmapFactory.decodeStream(it) } }
        .getOrNull()

internal fun fallbackBitmap(url: String): Bitmap {
    val size = 256
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    val colors = intArrayOf(
        0xFF1565C0.toInt(), 0xFF2E7D32.toInt(), 0xFF6A1B9A.toInt(),
        0xFFC62828.toInt(), 0xFFE65100.toInt(), 0xFF00695C.toInt(), 0xFF37474F.toInt()
    )
    val domain = url.removePrefix("https://").removePrefix("http://").split("/").first()
    val bg = colors[Math.abs(domain.hashCode()) % colors.size]
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = bg
    canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), paint)
    val letter = domain.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    paint.color = android.graphics.Color.WHITE
    paint.textSize = size * 0.45f
    paint.textAlign = Paint.Align.CENTER
    paint.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
    val textY = size / 2f - (paint.descent() + paint.ascent()) / 2f
    canvas.drawText(letter, size / 2f, textY, paint)
    return bmp
}
