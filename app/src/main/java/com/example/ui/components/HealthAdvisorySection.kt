package com.example.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.AqiLevel

/**
 * Health Advisory Section mapped to Apple Health Advisory Card.
 */
@Composable
fun HealthAdvisorySection(
    level: AqiLevel,
    modifier: Modifier = Modifier
) {
    AppleHealthAdvisoryCard(
        level = level,
        modifier = modifier
    )
}
