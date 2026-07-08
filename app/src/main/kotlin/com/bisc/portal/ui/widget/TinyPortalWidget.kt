package com.bisc.portal.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.RemoteViews
import com.bisc.portal.R

class TinyPortalWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) updateWidget(context, appWidgetManager, id)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        for (id in appWidgetIds) {
            editor.remove("$KEY_URL$id").remove("$KEY_ICON_ASSET$id").remove("$KEY_ICON_URI$id")
        }
        editor.apply()
    }

    companion object {
        const val PREFS = "tiny_portals"
        const val KEY_URL = "url_"
        const val KEY_ICON_ASSET = "icon_asset_"
        const val KEY_ICON_URI = "icon_uri_"

        fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int) {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val url       = prefs.getString("$KEY_URL$widgetId", "")        ?: ""
            val iconAsset = prefs.getString("$KEY_ICON_ASSET$widgetId", "") ?: ""
            val iconUri   = prefs.getString("$KEY_ICON_URI$widgetId", "")   ?: ""

            val views = RemoteViews(context.packageName, R.layout.widget_tiny_portal)
            val bitmap = when {
                iconAsset.isNotEmpty() -> loadAssetBitmap(context, iconAsset)
                iconUri.isNotEmpty()   -> BitmapFactory.decodeFile(iconUri)
                else                   -> null
            } ?: fallbackBitmap(url)
            views.setImageViewBitmap(R.id.widget_icon, bitmap)

            if (url.isNotEmpty()) {
                val pi = PendingIntent.getActivity(
                    context, widgetId,
                    Intent(Intent.ACTION_VIEW, Uri.parse(url)),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.widget_icon, pi)
            }
            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}
