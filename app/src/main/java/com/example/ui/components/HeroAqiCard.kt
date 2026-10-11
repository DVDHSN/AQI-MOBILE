package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.AqiStandard
import com.example.data.model.AtmosphericDrivers
import com.example.data.model.CurrentAirQuality
import com.example.data.model.LocationItem

/**
 * Hero AQI Card mapped to Apple Weather Hero Card design.
 */
@Composable
fun HeroAqiCard(
    location: LocationItem,
    current: CurrentAirQuality,
    standard: AqiStandard = AqiStandard.US_EPA,
    drivers: AtmosphericDrivers? = null,
    onToggleSaveLocation: (LocationItem) -> Unit,
    onCycleStandard: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AppleWeatherHeroCard(
        location = location,
        current = current,
        standard = standard,
        onToggleSaveLocation = onToggleSaveLocation,
        onCycleStandard = onCycleStandard,
        modifier = modifier
    )
}
