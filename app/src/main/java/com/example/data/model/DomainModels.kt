package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationItem(
    val id: String,
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val region: String? = null,
    val isCurrentLocation: Boolean = false,
    val isSaved: Boolean = false
) {
    val displayName: String
        get() = if (region.isNullOrBlank()) "$name, $country" else "$name, $region, $country"
}

enum class AqiStandard(
    val shortLabel: String,
    val fullTitle: String,
    val description: String
) {
    US_EPA("US AQI", "US Air Quality Index", "0–500 standard scale"),
    EUROPEAN("EUR AQI", "European Air Quality Index", "1–5 standard scale"),
    WHO("WHO Standard", "WHO PM2.5 Guideline", "Ratio relative to WHO 24h limit (15 μg/m³)")
}

enum class AqiLevel(
    val label: String,
    val minVal: Int,
    val maxVal: Int,
    val color: Color,
    val shortAdvice: String,
    val detailedAdvisory: String,
    val exerciseAdvice: String,
    val ventilationAdvice: String,
    val maskAdvice: String
) {
    GOOD(
        label = "GOOD",
        minVal = 0,
        maxVal = 50,
        color = AqiGood,
        shortAdvice = "Air quality is ideal for all outdoor activities.",
        detailedAdvisory = "Pollution concentrations are well within international safety standards. Risk to human health is negligible.",
        exerciseAdvice = "Safe for intensive outdoor running and endurance training.",
        ventilationAdvice = "Excellent conditions to open windows and ventilate homes.",
        maskAdvice = "No face covering required."
    ),
    MODERATE(
        label = "MODERATE",
        minVal = 51,
        maxVal = 100,
        color = AqiModerate,
        shortAdvice = "Acceptable air quality; sensitive groups may feel mild irritation.",
        detailedAdvisory = "A small number of individuals unusually sensitive to ozone or particulates may experience respiratory symptoms.",
        exerciseAdvice = "General population safe; unusually sensitive runners should monitor exertion.",
        ventilationAdvice = "Window ventilation acceptable during midday hours.",
        maskAdvice = "Optional for highly sensitive or asthmatic individuals."
    ),
    SENSITIVE(
        label = "UNHEALTHY (SENSITIVE)",
        minVal = 101,
        maxVal = 150,
        color = AqiSensitive,
        shortAdvice = "Sensitive groups should limit prolonged outdoor exertion.",
        detailedAdvisory = "Members of sensitive groups (children, elderly, people with heart or lung disease) may experience health effects. General public less likely affected.",
        exerciseAdvice = "Reduce heavy outdoor exertion; take frequent rest intervals.",
        ventilationAdvice = "Close windows during traffic peaks; use HEPA air purification.",
        maskAdvice = "KN95/N95 masks recommended for sensitive groups outdoors."
    ),
    UNHEALTHY(
        label = "UNHEALTHY",
        minVal = 151,
        maxVal = 200,
        color = AqiUnhealthy,
        shortAdvice = "Everyone may begin to experience adverse health effects.",
        detailedAdvisory = "Air quality is degraded. Prolonged exposure increases risk of cardiovascular and respiratory irritation across all demographics.",
        exerciseAdvice = "Avoid strenuous outdoor exercise; switch to indoor training.",
        ventilationAdvice = "Keep windows and exterior doors shut. Run air filtration systems.",
        maskAdvice = "Well-fitted particle filtration mask (N95/FFP2) advised outdoors."
    ),
    VERY_UNHEALTHY(
        label = "VERY UNHEALTHY",
        minVal = 201,
        maxVal = 300,
        color = AqiVeryUnhealthy,
        shortAdvice = "Health alert: serious risk of respiratory impact.",
        detailedAdvisory = "High particulate or gaseous contamination triggers widespread physiological stress. Everyone should curtail outdoor presence.",
        exerciseAdvice = "Cease all outdoor athletic and physical activities.",
        ventilationAdvice = "Seal living spaces against outdoor ambient drafts.",
        maskAdvice = "Certified particulate respirator strongly advised if traveling outdoors."
    ),
    HAZARDOUS(
        label = "HAZARDOUS",
        minVal = 301,
        maxVal = 9999,
        color = AqiHazardous,
        shortAdvice = "Emergency health warning: entire population at severe risk.",
        detailedAdvisory = "Extreme atmospheric pollution crisis. Immediate systemic inflammation risk upon ambient inhalation.",
        exerciseAdvice = "Remain strictly indoors; avoid all physical strain.",
        ventilationAdvice = "Do not open windows under any circumstances.",
        maskAdvice = "N95 / FFP3 respirator mandatory for any essential outdoor transit."
    );

    companion object {
        fun fromUsAqi(value: Float?): AqiLevel {
            val v = (value ?: 0f).toInt()
            return when {
                v <= 50 -> GOOD
                v <= 100 -> MODERATE
                v <= 150 -> SENSITIVE
                v <= 200 -> UNHEALTHY
                v <= 300 -> VERY_UNHEALTHY
                else -> HAZARDOUS
            }
        }

        fun fromEuropeanAqi(value: Float?): AqiLevel {
            val v = (value ?: 1f).toInt()
            return when (v) {
                1 -> GOOD
                2 -> MODERATE
                3 -> SENSITIVE
                4 -> UNHEALTHY
                5 -> VERY_UNHEALTHY
                else -> if (v < 1) GOOD else HAZARDOUS
            }
        }
    }
}

enum class AllergenRiskLevel(
    val label: String,
    val color: Color,
    val advice: String
) {
    LOW("Low Risk", AqiGood, "Negligible allergen count; ideal for outdoors."),
    MODERATE("Moderate Risk", AqiModerate, "Mild sensitivity possible for asthma or hay fever sufferers."),
    ELEVATED("Elevated Risk", AqiSensitive, "Higher pollen density; sunglasses and preventative medication advised."),
    HIGH("High Risk", AqiUnhealthy, "Substantial allergen dispersion; keep windows closed.")
}

data class PollenItem(
    val name: String,
    val countGrains: Float,
    val risk: AllergenRiskLevel
)

data class AllergenReport(
    val overallRisk: AllergenRiskLevel,
    val dominantPollen: String,
    val pollens: List<PollenItem>
)

data class DailyHeatmapPoint(
    val date: String,
    val displayDate: String,
    val dayOfWeek: String,
    val avgAqi: Float,
    val level: AqiLevel
)

data class MonthlyHeatmapReport(
    val days: List<DailyHeatmapPoint>,
    val goodDaysCount: Int,
    val moderateDaysCount: Int,
    val degradedDaysCount: Int,
    val trendComparisonPercent: Int,
    val trendInsight: String
)

enum class ChartMetric(
    val title: String,
    val shortLabel: String,
    val unit: String,
    val whoThreshold: Float,
    val description: String
) {
    US_AQI(
        title = "US Air Quality Index",
        shortLabel = "US AQI",
        unit = "AQI",
        whoThreshold = 50f,
        description = "Composite EPA standard benchmark aggregating all major criteria pollutants."
    ),
    PM2_5(
        title = "Fine Particulate Matter (PM2.5)",
        shortLabel = "PM2.5",
        unit = "μg/m³",
        whoThreshold = 15f,
        description = "Microscopic particles ≤ 2.5 micrometers that penetrate deep into pulmonary alveoli."
    ),
    PM10(
        title = "Inhalable Particulate Matter (PM10)",
        shortLabel = "PM10",
        unit = "μg/m³",
        whoThreshold = 45f,
        description = "Coarse dust, pollen, and combustion debris ≤ 10 micrometers depositing in upper airways."
    ),
    OZONE(
        title = "Ground-level Ozone (O₃)",
        shortLabel = "O₃",
        unit = "μg/m³",
        whoThreshold = 100f,
        description = "Secondary photochemical smog created by UV reacting with vehicle emissions and VOCs."
    ),
    NO2(
        title = "Nitrogen Dioxide (NO₂)",
        shortLabel = "NO₂",
        unit = "μg/m³",
        whoThreshold = 25f,
        description = "Toxic brownish gas emitted by high-temperature thermal engines and industrial combustion."
    ),
    EUROPEAN_AQI(
        title = "European Air Quality Index",
        shortLabel = "EUR AQI",
        unit = "EAQI",
        whoThreshold = 40f,
        description = "European Environment Agency (EEA) standardized five-category index."
    );

    fun getValue(point: HourlyDataPoint): Float? {
        return when (this) {
            US_AQI -> point.usAqi
            PM2_5 -> point.pm25
            PM10 -> point.pm10
            OZONE -> point.ozone
            NO2 -> point.nitrogenDioxide
            EUROPEAN_AQI -> point.europeanAqi
        }
    }
}

data class CurrentAirQuality(
    val time: String,
    val usAqi: Float,
    val europeanAqi: Float,
    val pm25: Float,
    val pm10: Float,
    val ozone: Float,
    val nitrogenDioxide: Float,
    val sulphurDioxide: Float,
    val carbonMonoxide: Float,
    val alderPollen: Float = 0f,
    val birchPollen: Float = 0f,
    val grassPollen: Float = 0f,
    val mugwortPollen: Float = 0f,
    val olivePollen: Float = 0f,
    val ragweedPollen: Float = 0f
) {
    val level: AqiLevel get() = AqiLevel.fromUsAqi(usAqi)
}

data class HourlyDataPoint(
    val rawTime: String,
    val displayHour: String,
    val displayDate: String,
    val dayOfWeek: String,
    val usAqi: Float?,
    val europeanAqi: Float?,
    val pm25: Float?,
    val pm10: Float?,
    val ozone: Float?,
    val nitrogenDioxide: Float?,
    val sulphurDioxide: Float?,
    val carbonMonoxide: Float?,
    val alderPollen: Float? = null,
    val birchPollen: Float? = null,
    val grassPollen: Float? = null,
    val mugwortPollen: Float? = null,
    val olivePollen: Float? = null,
    val ragweedPollen: Float? = null
)

enum class TimelineMode(val title: String) {
    PAST_24_HOURS("24-HOUR TREND"),
    HISTORY("7-DAY HISTORY"),
    FORECAST("5-DAY FORECAST")
}

enum class AppMode(val title: String) {
    DISPATCH("DISPATCH"),
    DUEL("CITY DUEL")
}

data class DominantDriver(
    val formula: String,
    val name: String,
    val value: Float,
    val unit: String,
    val whoRatio: Float,
    val relativeSharePercent: Int,
    val impactAssessment: String
)

data class CleanestWindow(
    val startHour: String,
    val endHour: String,
    val dateLabel: String,
    val averageAqi: Float,
    val aqiLevel: AqiLevel,
    val workoutRecommendation: String
)

data class WindDispersion(
    val temperatureC: Float,
    val relativeHumidity: Float,
    val windSpeedKmh: Float,
    val windDirectionDegrees: Float,
    val windDirectionCompass: String,
    val stagnationRating: String,
    val dispersionNote: String
)

data class AtmosphericDrivers(
    val dominantDriver: DominantDriver,
    val cleanestWindow: CleanestWindow?,
    val windDispersion: WindDispersion?
)

data class AirQualityReport(
    val location: LocationItem,
    val current: CurrentAirQuality,
    val hourlyHistory: List<HourlyDataPoint>,
    val hourlyForecast: List<HourlyDataPoint> = emptyList(),
    val timezone: String,
    val drivers: AtmosphericDrivers? = null,
    val allergenReport: AllergenReport? = null,
    val heatmapReport: MonthlyHeatmapReport? = null
)
