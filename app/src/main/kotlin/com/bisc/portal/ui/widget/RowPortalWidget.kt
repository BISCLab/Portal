package com.bisc.portal.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import com.bisc.portal.R

class RowPortalWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) updateWidget(context, appWidgetManager, id)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        for (id in appWidgetIds) {
            for (slot in 1..5) {
                editor.remove("$KEY_URL${id}_$slot")
                    .remove("$KEY_ICON_ASSET${id}_$slot")
                    .remove("$KEY_ICON_URI${id}_$slot")
            }
        }
        editor.apply()
    }

    companion object {
        const val PREFS = "row_portals"
        const val KEY_URL = "url_"
        const val KEY_ICON_ASSET = "icon_asset_"
        const val KEY_ICON_URI = "icon_uri_"

        val SLOT_VIEW_IDS = listOf(
            R.id.row_icon_1, R.id.row_icon_2, R.id.row_icon_3,
            R.id.row_icon_4, R.id.row_icon_5
        )

        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val rv = RemoteViews(context.packageName, R.layout.widget_row_portal)

            SLOT_VIEW_IDS.forEachIndexed { idx, viewId ->
                val slot = idx + 1
                val url       = prefs.getString("$KEY_URL${widgetId}_$slot", "")        ?: ""
                val iconAsset = prefs.getString("$KEY_ICON_ASSET${widgetId}_$slot", "") ?: ""
                val iconUri   = prefs.getString("$KEY_ICON_URI${widgetId}_$slot", "")   ?: ""

                if (url.isNotBlank()) {
                    rv.setViewVisibility(viewId, View.VISIBLE)
                    val bmp = when {
                        iconAsset.isNotEmpty() -> loadAssetBitmap(context, iconAsset)
                        iconUri.isNotEmpty()   -> BitmapFactory.decodeFile(iconUri)
                        else                   -> null
                    } ?: fallbackBitmap(url)
                    rv.setImageViewBitmap(viewId, bmp)
                    val pi = PendingIntent.getActivity(
                        context, widgetId * 10 + slot,
                        Intent(Intent.ACTION_VIEW, Uri.parse(url)),
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    rv.setOnClickPendingIntent(viewId, pi)
                } else {
                    rv.setViewVisibility(viewId, View.INVISIBLE)
                }
            }

            appWidgetManager.updateAppWidget(widgetId, rv)
        }
    }
}
