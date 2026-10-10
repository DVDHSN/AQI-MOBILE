package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AirQualityResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null,
    @Json(name = "timezone_abbreviation") val timezoneAbbreviation: String? = null,
    val current: CurrentAirQualityDto? = null,
    val hourly: HourlyAirQualityDto? = null
)

@JsonClass(generateAdapter = true)
data class CurrentAirQualityDto(
    val time: String? = null,
    val interval: Int? = null,
    @Json(name = "us_aqi") val usAqi: Float? = null,
    @Json(name = "european_aqi") val europeanAqi: Float? = null,
    @Json(name = "pm10") val pm10: Float? = null,
    @Json(name = "pm2_5") val pm25: Float? = null,
    val ozone: Float? = null,
    @Json(name = "nitrogen_dioxide") val nitrogenDioxide: Float? = null,
    @Json(name = "sulphur_dioxide") val sulphurDioxide: Float? = null,
    @Json(name = "carbon_monoxide") val carbonMonoxide: Float? = null,
    @Json(name = "alder_pollen") val alderPollen: Float? = null,
    @Json(name = "birch_pollen") val birchPollen: Float? = null,
    @Json(name = "grass_pollen") val grassPollen: Float? = null,
    @Json(name = "mugwort_pollen") val mugwortPollen: Float? = null,
    @Json(name = "olive_pollen") val olivePollen: Float? = null,
    @Json(name = "ragweed_pollen") val ragweedPollen: Float? = null
)

@JsonClass(generateAdapter = true)
data class HourlyAirQualityDto(
    val time: List<String> = emptyList(),
    @Json(name = "us_aqi") val usAqi: List<Float?> = emptyList(),
    @Json(name = "european_aqi") val europeanAqi: List<Float?> = emptyList(),
    @Json(name = "pm10") val pm10: List<Float?> = emptyList(),
    @Json(name = "pm2_5") val pm25: List<Float?> = emptyList(),
    val ozone: List<Float?> = emptyList(),
    @Json(name = "nitrogen_dioxide") val nitrogenDioxide: List<Float?> = emptyList(),
    @Json(name = "sulphur_dioxide") val sulphurDioxide: List<Float?> = emptyList(),
    @Json(name = "carbon_monoxide") val carbonMonoxide: List<Float?> = emptyList(),
    @Json(name = "alder_pollen") val alderPollen: List<Float?> = emptyList(),
    @Json(name = "birch_pollen") val birchPollen: List<Float?> = emptyList(),
    @Json(name = "grass_pollen") val grassPollen: List<Float?> = emptyList(),
    @Json(name = "mugwort_pollen") val mugwortPollen: List<Float?> = emptyList(),
    @Json(name = "olive_pollen") val olivePollen: List<Float?> = emptyList(),
    @Json(name = "ragweed_pollen") val ragweedPollen: List<Float?> = emptyList()
)

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    val results: List<GeocodingResultDto>? = null
)

@JsonClass(generateAdapter = true)
data class GeocodingResultDto(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String? = null,
    @Json(name = "admin1") val admin1: String? = null,
    @Json(name = "country_code") val countryCode: String? = null,
    val elevation: Float? = null
)

@JsonClass(generateAdapter = true)
data class WeatherForecastResponse(
    val latitude: Double,
    val longitude: Double,
    val current: CurrentWeatherDto? = null
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherDto(
    val time: String? = null,
    @Json(name = "temperature_2m") val temperature: Float? = null,
    @Json(name = "relative_humidity_2m") val humidity: Float? = null,
    @Json(name = "wind_speed_10m") val windSpeed: Float? = null,
    @Json(name = "wind_direction_10m") val windDirection: Float? = null
)
