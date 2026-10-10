package com.example.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import com.example.data.model.LocationItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

class LocationTracker(private val context: Context) {
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationItem(): LocationItem? = withContext(Dispatchers.IO) {
        val location = getDeviceLocation() ?: return@withContext null
        val lat = location.latitude
        val lon = location.longitude

        val (name, region, country) = resolvePlaceName(lat, lon)

        LocationItem(
            id = "current_gps_location",
            name = name,
            country = country,
            latitude = lat,
            longitude = lon,
            region = region,
            isCurrentLocation = true,
            isSaved = false
        )
    }

    @SuppressLint("MissingPermission")
    private suspend fun getDeviceLocation(): Location? {
        val mgr = locationManager ?: return null

        val lastGps = runCatching { mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER) }.getOrNull()
        val lastNetwork = runCatching { mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) }.getOrNull()
        val lastPassive = runCatching { mgr.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER) }.getOrNull()

        val candidates = listOfNotNull(lastGps, lastNetwork, lastPassive)
        val bestLast = candidates.maxByOrNull { it.time }

        // If reasonably recent (< 10 minutes), reuse immediately
        val tenMinutesAgo = System.currentTimeMillis() - (10 * 60 * 1000)
        if (bestLast != null && bestLast.time > tenMinutesAgo) {
            return bestLast
        }

        return suspendCancellableCoroutine { cont ->
            val listener = object : LocationListener {
                override fun onLocationChanged(loc: Location) {
                    mgr.removeUpdates(this)
                    if (cont.isActive) cont.resume(loc)
                }
                @Deprecated("Deprecated in Java")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            val provider = when {
                mgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                mgr.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                else -> null
            }

            if (provider == null) {
                cont.resume(bestLast)
                return@suspendCancellableCoroutine
            }

            try {
                mgr.requestSingleUpdate(provider, listener, Looper.getMainLooper())
                cont.invokeOnCancellation {
                    mgr.removeUpdates(listener)
                }
            } catch (_: SecurityException) {
                cont.resume(bestLast)
            } catch (_: Exception) {
                cont.resume(bestLast)
            }
        }
    }

    private fun resolvePlaceName(latitude: Double, longitude: Double): Triple<String, String?, String> {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            val address = addresses?.firstOrNull()
            if (address != null) {
                val city = address.locality
                    ?: address.subAdminArea
                    ?: address.adminArea
                    ?: "Current Location"
                val region = address.adminArea
                val country = address.countryName ?: address.countryCode ?: "Local"
                Triple(city, region, country)
            } else {
                Triple("Current Location", null, "GPS")
            }
        } catch (_: Exception) {
            Triple("Current Location", null, "GPS")
        }
    }
}
