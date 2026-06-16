package com.pec.kekefit

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Build
import android.widget.RemoteViews

class KekeFitWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            actualizarWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun actualizarWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val prefs = context.applicationContext.getSharedPreferences(
                "keke_fit_widget_prefs",
                Context.MODE_PRIVATE
            )

            val nombre = prefs.getString("nombre_usuario", "KekeFit")
                ?.takeIf { it.isNotBlank() }
                ?: "KekeFit"

            val diasRacha = prefs.getInt("dias_racha", 0).coerceAtLeast(0)
            val caloriasHoy = prefs.getInt("calorias_hoy", 0).coerceAtLeast(0)

            val views = RemoteViews(context.packageName, R.layout.keke_fit_widget)

            views.setTextViewText(R.id.tvWidgetName, nombre)
            views.setTextViewText(R.id.tvWidgetStreak, "$diasRacha días de racha")
            views.setTextViewText(R.id.tvWidgetCalories, "$caloriasHoy kcal hoy")

            val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            if (launchIntent != null) {
                val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }

                val pendingIntent = PendingIntent.getActivity(
                    context,
                    1001,
                    launchIntent,
                    flags
                )

                views.setOnClickPendingIntent(R.id.kekeWidgetCard, pendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
