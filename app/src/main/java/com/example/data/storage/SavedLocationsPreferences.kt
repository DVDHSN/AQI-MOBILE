package com.example.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.LocationItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class SavedLocationsPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("atmosphere_saved_locations", Context.MODE_PRIVATE)

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val listType = Types.newParameterizedType(List::class.java, LocationItem::class.java)
    private val adapter = moshi.adapter<List<LocationItem>>(listType)

    fun getSavedLocations(): List<LocationItem> {
        val json = prefs.getString(KEY_SAVED, null) ?: return emptyList()
        return try {
            adapter.fromJson(json) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveLocation(location: LocationItem): List<LocationItem> {
        val current = getSavedLocations().toMutableList()
        val index = current.indexOfFirst { it.id == location.id }
        if (index == -1) {
            current.add(0, location.copy(isSaved = true))
        } else {
            current[index] = location.copy(isSaved = true)
        }
        prefs.edit().putString(KEY_SAVED, adapter.toJson(current)).apply()
        return current
    }

    fun removeSavedLocation(locationId: String): List<LocationItem> {
        val current = getSavedLocations().filterNot { it.id == locationId }
        prefs.edit().putString(KEY_SAVED, adapter.toJson(current)).apply()
        return current
    }

    fun isLocationSaved(locationId: String): Boolean {
        return getSavedLocations().any { it.id == locationId }
    }

    companion object {
        private const val KEY_SAVED = "saved_locations_json"
    }
}
