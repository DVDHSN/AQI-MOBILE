package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import com.example.MainActivity
import com.example.R
import com.example.data.model.AqiLevel
import com.example.data.model.LocationItem
import com.example.data.repository.AirQualityRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AqiAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidgetView(
                context = context,
                appWidgetManager = appWidgetManager,
                appWidgetId = appWidgetId,
                cityName = lastKnownCity,
                aqi = lastKnownAqi,
                levelLabel = lastKnownLevelLabel,
                levelColorHex = lastKnownColorHex,
                bestWindow = lastKnownBestWindow
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ACTION_REFRESH_WIDGET) {
            // Trigger background refresh for current/default location
            CoroutineScope(Dispatchers.IO).launch {
                val repo = AirQualityRepository()
                val targetCity = LocationItem("current", lastKnownCity, "", 48.8566, 2.3522)
                val result = repo.fetchAirQuality(targetCity)
                result.getOrNull()?.let { report ->
                    val aqi = report.current.usAqi.toInt()
                    val level = report.current.level
                    val bestWindow = report.drivers?.cleanestWindow?.let {
                        "Best window: ${it.startHour} - ${it.endHour}"
                    } ?: "Air conditions updated"

                    pushWidgetUpdate(
                        context = context,
                        locationName = report.location.name,
                        aqi = aqi,
                        level = level,
                        bestWindowText = bestWindow
                    )
                }
            }
        }
    }

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.example.action.REFRESH_WIDGET"

        private var lastKnownCity: String = "Atmosphere"
        private var lastKnownAqi: Int = 42
        private var lastKnownLevelLabel: String = "GOOD"
        private var lastKnownColorHex: Int = Color.parseColor("#10B981")
        private var lastKnownBestWindow: String = "Best window: 6 AM - 11 AM"

        fun pushWidgetUpdate(
            context: Context,
            locationName: String,
            aqi: Int,
            level: AqiLevel,
            bestWindowText: String
        ) {
            lastKnownCity = locationName
            lastKnownAqi = aqi
            lastKnownLevelLabel = level.label
            lastKnownColorHex = when (level) {
                AqiLevel.GOOD -> Color.parseColor("#10B981")
                AqiLevel.MODERATE -> Color.parseColor("#F59E0B")
                AqiLevel.SENSITIVE -> Color.parseColor("#F97316")
                AqiLevel.UNHEALTHY -> Color.parseColor("#EF4444")
                AqiLevel.VERY_UNHEALTHY -> Color.parseColor("#A855F7")
                AqiLevel.HAZARDOUS -> Color.parseColor("#E11D48")
            }
            lastKnownBestWindow = bestWindowText

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, AqiAppWidgetProvider::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)

            for (widgetId in allWidgetIds) {
                updateWidgetView(
                    context = context,
                    appWidgetManager = appWidgetManager,
                    appWidgetId = widgetId,
                    cityName = lastKnownCity,
                    aqi = lastKnownAqi,
                    levelLabel = lastKnownLevelLabel,
                    levelColorHex = lastKnownColorHex,
                    bestWindow = lastKnownBestWindow
                )
            }
        }

        private fun updateWidgetView(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            cityName: String,
            aqi: Int,
            levelLabel: String,
            levelColorHex: Int,
            bestWindow: String
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_aqi_layout)

            views.setTextViewText(R.id.widget_city_name, cityName)
            views.setTextViewText(R.id.widget_aqi_value, aqi.toString())
            views.setTextViewText(R.id.widget_aqi_level, levelLabel)
            views.setTextColor(R.id.widget_aqi_level, levelColorHex)
            views.setTextViewText(R.id.widget_best_window, bestWindow)

            // Click on widget opens app
            val appIntent = Intent(context, MainActivity::class.java)
            val appPendingIntent = PendingIntent.getActivity(
                context,
                0,
                appIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, appPendingIntent)

            // Click on refresh icon triggers widget refresh broadcast
            val refreshIntent = Intent(context, AqiAppWidgetProvider::class.java).apply {
                action = ACTION_REFRESH_WIDGET
            }
            val refreshPendingIntent = PendingIntent.getBroadcast(
                context,
                1,
                refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
