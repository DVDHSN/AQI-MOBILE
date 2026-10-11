package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.CurrentAirQuality

/**
 * Pollutant Breakdown Grid mapped to Apple Pollutants Grid Section.
 */
@Composable
fun PollutantMatrix(
    current: CurrentAirQuality,
    modifier: Modifier = Modifier
) {
    ApplePollutantsGridSection(
        current = current,
        modifier = modifier
    )
}
