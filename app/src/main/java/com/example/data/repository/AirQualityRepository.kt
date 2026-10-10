package com.example.data.repository

import com.example.data.model.*
import com.example.data.network.AirQualityApiService
import com.example.data.network.GeocodingApiService
import com.example.data.network.NetworkClient
import com.example.data.network.WeatherApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

class AirQualityRepository(
    private val airQualityApi: AirQualityApiService = NetworkClient.airQualityApi,
    private val geocodingApi: GeocodingApiService = NetworkClient.geocodingApi,
    private val weatherApi: WeatherApiService = NetworkClient.weatherApi
) {

    val curatedMegacities: List<LocationItem> = listOf(
        LocationItem("tokyo", "Tokyo", "Japan", 35.6762, 139.6503, "Kanto"),
        LocationItem("new_york", "New York", "United States", 40.7128, -74.0060, "New York"),
        LocationItem("london", "London", "United Kingdom", 51.5074, -0.1278, "Greater London"),
        LocationItem("delhi", "Delhi", "India", 28.6139, 77.2090, "Delhi"),
        LocationItem("paris", "Paris", "France", 48.8566, 2.3522, "Île-de-France"),
        LocationItem("beijing", "Beijing", "China", 39.9042, 116.4074, "Beijing"),
        LocationItem("sao_paulo", "São Paulo", "Brazil", -23.5505, -46.6333, "São Paulo"),
        LocationItem("cairo", "Cairo", "Egypt", 30.0444, 31.2357, "Cairo Governorate"),
        LocationItem("sydney", "Sydney", "Australia", -33.8688, 151.2093, "New South Wales")
    )

    private val isoParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US)
    private val hourFormatter = SimpleDateFormat("HH:mm", Locale.US)
    private val dateFormatter = SimpleDateFormat("MMM d", Locale.US)
    private val dayOfWeekFormatter = SimpleDateFormat("EEE", Locale.US)

    suspend fun fetchAirQuality(location: LocationItem): Result<AirQualityReport> =
        withContext(Dispatchers.IO) {
            try {
                // Fetch air quality and weather in parallel
                val airQualityDeferred = async {
                    airQualityApi.getAirQuality(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        pastDays = 7,
                        forecastDays = 5
                    )
                }

                val weatherDeferred = async {
                    runCatching {
                        weatherApi.getCurrentWeather(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    }.getOrNull()
                }

                val response = airQualityDeferred.await()
                val weatherResponse = weatherDeferred.await()

                val currentDto = response.current
                    ?: return@withContext Result.failure(Exception("Missing current air quality data from Open-Meteo"))

                val current = CurrentAirQuality(
                    time = currentDto.time ?: "",
                    usAqi = currentDto.usAqi ?: 0f,
                    europeanAqi = currentDto.europeanAqi ?: 0f,
                    pm25 = currentDto.pm25 ?: 0f,
                    pm10 = currentDto.pm10 ?: 0f,
                    ozone = currentDto.ozone ?: 0f,
                    nitrogenDioxide = currentDto.nitrogenDioxide ?: 0f,
                    sulphurDioxide = currentDto.sulphurDioxide ?: 0f,
                    carbonMonoxide = currentDto.carbonMonoxide ?: 0f
                )

                val hourlyDto = response.hourly
                val allHourlyList = mutableListOf<HourlyDataPoint>()

                if (hourlyDto != null && hourlyDto.time.isNotEmpty()) {
                    val count = hourlyDto.time.size
                    for (i in 0 until count) {
                        val rawTime = hourlyDto.time[i]
                        val (displayHour, displayDate, dayOfWeek) = parseTimeLabels(rawTime)

                        allHourlyList.add(
                            HourlyDataPoint(
                                rawTime = rawTime,
                                displayHour = displayHour,
                                displayDate = displayDate,
                                dayOfWeek = dayOfWeek,
                                usAqi = hourlyDto.usAqi.getOrNull(i),
                                europeanAqi = hourlyDto.europeanAqi.getOrNull(i),
                                pm25 = hourlyDto.pm25.getOrNull(i),
                                pm10 = hourlyDto.pm10.getOrNull(i),
                                ozone = hourlyDto.ozone.getOrNull(i),
                                nitrogenDioxide = hourlyDto.nitrogenDioxide.getOrNull(i),
                                sulphurDioxide = hourlyDto.sulphurDioxide.getOrNull(i),
                                carbonMonoxide = hourlyDto.carbonMonoxide.getOrNull(i)
                            )
                        )
                    }
                }

                // Partition allHourlyList into History (past 7 days up to now) and Forecast (now to next 5 days)
                val currentTimeStr = currentDto.time ?: ""
                val currentIndex = allHourlyList.indexOfFirst { it.rawTime >= currentTimeStr }
                    .takeIf { it >= 0 } ?: (allHourlyList.size / 2)

                val historyList = allHourlyList.subList(0, (currentIndex + 1).coerceAtMost(allHourlyList.size))
                val forecastList = allHourlyList.subList(currentIndex.coerceAtMost(allHourlyList.size), allHourlyList.size)

                // 1. Dominant Driver computation
                val dominantDriver = computeDominantDriver(current)

                // 2. Cleanest Workout Window computation (next 24 hours of forecast)
                val cleanestWindow = computeCleanestWindow(forecastList)

                // 3. Wind & Dispersion computation
                val windDispersion = computeWindDispersion(weatherResponse)

                val drivers = AtmosphericDrivers(
                    dominantDriver = dominantDriver,
                    cleanestWindow = cleanestWindow,
                    windDispersion = windDispersion
                )

                Result.success(
                    AirQualityReport(
                        location = location,
                        current = current,
                        hourlyHistory = historyList,
                        hourlyForecast = forecastList,
                        timezone = response.timezone ?: "UTC",
                        drivers = drivers
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun computeDominantDriver(current: CurrentAirQuality): DominantDriver {
        val candidates = listOf(
            Triple("PM2.5", "Fine Particulates", current.pm25 / 15f),
            Triple("PM10", "Inhalable Dust", current.pm10 / 45f),
            Triple("O₃", "Surface Ozone", current.ozone / 100f),
            Triple("NO₂", "Nitrogen Dioxide", current.nitrogenDioxide / 25f),
            Triple("SO₂", "Sulphur Dioxide", current.sulphurDioxide / 40f),
            Triple("CO", "Carbon Monoxide", current.carbonMonoxide / 4000f)
        )

        val totalScore = candidates.sumOf { it.third.toDouble() }.toFloat().coerceAtLeast(0.01f)
        val highest = candidates.maxByOrNull { it.third } ?: candidates.first()

        val sharePercent = ((highest.third / totalScore) * 100f).roundToInt().coerceIn(15, 95)

        val (formula, name, ratio) = highest
        val rawVal = when (formula) {
            "PM2.5" -> current.pm25
            "PM10" -> current.pm10
            "O₃" -> current.ozone
            "NO₂" -> current.nitrogenDioxide
            "SO₂" -> current.sulphurDioxide
            else -> current.carbonMonoxide
        }

        val assessment = when {
            ratio <= 1.0f -> "Within WHO guideline threshold. Minor contribution to overall index."
            ratio <= 2.0f -> "Exceeds daily guideline. Primary driver of mild airway irritation."
            ratio <= 3.5f -> "Significantly elevated above WHO limit. Chief contributor to degradation."
            else -> "Severe elevation. Demands personal respiratory avoidance protocols."
        }

        return DominantDriver(
            formula = formula,
            name = name,
            value = rawVal,
            unit = "μg/m³",
            whoRatio = ratio,
            relativeSharePercent = sharePercent,
            impactAssessment = assessment
        )
    }

    private fun computeCleanestWindow(forecast: List<HourlyDataPoint>): CleanestWindow? {
        val next24h = forecast.take(24)
        if (next24h.size < 3) return null

        var bestAvg = Float.MAX_VALUE
        var bestIndex = 0

        for (i in 0..(next24h.size - 3)) {
            val window = next24h.subList(i, i + 3)
            val avg = window.mapNotNull { it.usAqi }.average().toFloat()
            if (avg < bestAvg) {
                bestAvg = avg
                bestIndex = i
            }
        }

        val start = next24h[bestIndex]
        val end = next24h[bestIndex + 2]
        val aqiLevel = AqiLevel.fromUsAqi(bestAvg)

        val workoutAdvice = when (aqiLevel) {
            AqiLevel.GOOD -> "Prime window for high-aerobic training, cycling, and long runs."
            AqiLevel.MODERATE -> "Acceptable for light to moderate outdoor runs; hydrate well."
            AqiLevel.SENSITIVE -> "Limit exertion; choose indoor gym training if sensitive."
            else -> "Pollution remains elevated; outdoor athletic training discouraged."
        }

        return CleanestWindow(
            startHour = start.displayHour,
            endHour = end.displayHour,
            dateLabel = "${start.dayOfWeek} ${start.displayDate}",
            averageAqi = bestAvg,
            aqiLevel = aqiLevel,
            workoutRecommendation = workoutAdvice
        )
    }

    private fun computeWindDispersion(weather: WeatherForecastResponse?): WindDispersion? {
        val current = weather?.current ?: return null
        val speed = current.windSpeed ?: 0f
        val dir = current.windDirection ?: 0f
        val temp = current.temperature ?: 20f
        val hum = current.humidity ?: 50f

        val compass = when {
            dir in 22.5..67.5 -> "NE"
            dir in 67.5..112.5 -> "E"
            dir in 112.5..157.5 -> "SE"
            dir in 157.5..202.5 -> "S"
            dir in 202.5..247.5 -> "SW"
            dir in 247.5..292.5 -> "W"
            dir in 292.5..337.5 -> "NW"
            else -> "N"
        }

        val (stagnationRating, note) = when {
            speed < 6f -> Pair(
                "HIGH STAGNATION",
                "Light thermal inversion traps particulates within local urban basin."
            )
            speed in 6f..18f -> Pair(
                "STEADY VENTILATION",
                "Moderate surface breezes promoting continuous atmospheric turnover."
            )
            else -> Pair(
                "RAPID DISPERSAL",
                "Brisk regional wind currents actively flushing criteria pollutants."
            )
        }

        return WindDispersion(
            temperatureC = temp,
            relativeHumidity = hum,
            windSpeedKmh = speed,
            windDirectionDegrees = dir,
            windDirectionCompass = compass,
            stagnationRating = stagnationRating,
            dispersionNote = note
        )
    }

    suspend fun searchLocations(query: String): Result<List<LocationItem>> =
        withContext(Dispatchers.IO) {
            try {
                if (query.trim().length < 2) {
                    return@withContext Result.success(emptyList())
                }
                val response = geocodingApi.searchLocations(query = query.trim())
                val items = response.results.orEmpty().map { dto ->
                    LocationItem(
                        id = "geo_${dto.id}",
                        name = dto.name,
                        country = dto.country ?: dto.countryCode ?: "",
                        latitude = dto.latitude,
                        longitude = dto.longitude,
                        region = dto.admin1
                    )
                }
                Result.success(items)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun parseTimeLabels(rawTime: String): Triple<String, String, String> {
        return try {
            val date = isoParser.parse(rawTime)
            if (date != null) {
                Triple(
                    hourFormatter.format(date),
                    dateFormatter.format(date),
                    dayOfWeekFormatter.format(date)
                )
            } else {
                Triple(rawTime.takeLast(5), rawTime.take(10), "")
            }
        } catch (_: Exception) {
            Triple(rawTime.takeLast(5), rawTime.take(10), "")
        }
    }
}
