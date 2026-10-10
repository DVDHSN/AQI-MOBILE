package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.*
import com.example.data.repository.AirQualityRepository
import com.example.data.storage.SavedLocationsPreferences
import com.example.ui.viewmodel.AqiViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Atmosphere", appName)
    }

    @Test
    fun `verify curated megacities present in repository`() {
        val repository = AirQualityRepository()
        assertTrue(repository.curatedMegacities.isNotEmpty())
        val tokyo = repository.curatedMegacities.firstOrNull { it.name == "Tokyo" }
        assertNotNull(tokyo)
        assertEquals(35.6762, tokyo!!.latitude, 0.001)
    }

    @Test
    fun `test AqiLevel calculation`() {
        assertEquals(AqiLevel.GOOD, AqiLevel.fromUsAqi(25f))
        assertEquals(AqiLevel.MODERATE, AqiLevel.fromUsAqi(75f))
        assertEquals(AqiLevel.SENSITIVE, AqiLevel.fromUsAqi(125f))
        assertEquals(AqiLevel.UNHEALTHY, AqiLevel.fromUsAqi(175f))
        assertEquals(AqiLevel.VERY_UNHEALTHY, AqiLevel.fromUsAqi(250f))
        assertEquals(AqiLevel.HAZARDOUS, AqiLevel.fromUsAqi(400f))
    }

    @Test
    fun `test ChartMetric extraction`() {
        val point = HourlyDataPoint(
            rawTime = "2026-10-09T12:00",
            displayHour = "12:00",
            displayDate = "Oct 9",
            dayOfWeek = "Fri",
            usAqi = 45f,
            europeanAqi = 30f,
            pm25 = 12.5f,
            pm10 = 25f,
            ozone = 60f,
            nitrogenDioxide = 20f,
            sulphurDioxide = 5f,
            carbonMonoxide = 300f
        )
        assertEquals(45f, ChartMetric.US_AQI.getValue(point))
        assertEquals(12.5f, ChartMetric.PM2_5.getValue(point))
        assertEquals(25f, ChartMetric.PM10.getValue(point))
        assertEquals(60f, ChartMetric.OZONE.getValue(point))
        assertEquals(20f, ChartMetric.NO2.getValue(point))
        assertEquals(30f, ChartMetric.EUROPEAN_AQI.getValue(point))
    }

    @Test
    fun `test saved locations persistence`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = SavedLocationsPreferences(context)
        val testLoc = LocationItem(
            id = "test_city_berlin",
            name = "Berlin",
            country = "Germany",
            latitude = 52.5200,
            longitude = 13.4050
        )

        // Save
        prefs.saveLocation(testLoc)
        assertTrue(prefs.isLocationSaved(testLoc.id))
        assertTrue(prefs.getSavedLocations().any { it.id == testLoc.id })

        // Remove
        prefs.removeSavedLocation(testLoc.id)
        assertFalse(prefs.isLocationSaved(testLoc.id))
    }

    @Test
    fun `test AqiViewModel modes and state transitions`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = AqiViewModel(app)
        assertNotNull(viewModel)
        assertEquals(AppMode.DISPATCH, viewModel.uiState.value.appMode)
        assertEquals(TimelineMode.HISTORY, viewModel.uiState.value.timelineMode)

        // Mode switch
        viewModel.setAppMode(AppMode.DUEL)
        assertEquals(AppMode.DUEL, viewModel.uiState.value.appMode)
        assertNotNull(viewModel.uiState.value.duelCityA)
        assertNotNull(viewModel.uiState.value.duelCityB)

        // Timeline switch
        viewModel.setTimelineMode(TimelineMode.FORECAST)
        assertEquals(TimelineMode.FORECAST, viewModel.uiState.value.timelineMode)
    }
}
