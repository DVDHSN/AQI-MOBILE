package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AtmosphericDrivers
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun AtmosphericDriversSection(
    drivers: AtmosphericDrivers,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .border(1.dp, HairlineBorder)
            .padding(16.dp)
            .testTag("atmospheric_drivers_section")
    ) {
        // Section Header (Vertical stack - zero horizontal collision)
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "ATMOSPHERIC DYNAMICS",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.5.sp,
                    color = EditorialBlue,
                    fontSize = 10.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "ENVIRONMENTAL INTELLIGENCE",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = InkWhite,
                    letterSpacing = 0.5.sp,
                    fontSize = 15.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Card 1: Dominant Driver Card
        val driver = drivers.dominantDriver
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkElevated)
                .border(1.dp, HairlineBorder)
                .padding(14.dp)
        ) {
            // Category row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = EditorialBlue,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "01 // DOMINANT POLLUTANT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EditorialBlue,
                            letterSpacing = 0.8.sp,
                            fontSize = 10.sp
                        )
                    )
                }

                Text(
                    text = "${driver.relativeSharePercent}% OF IMPACT",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = InkMuted,
                        fontSize = 10.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Value & Multiplier row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${driver.formula} (${driver.name})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = InkWhite,
                        fontSize = 15.sp
                    )
                )

                Box(
                    modifier = Modifier
                        .background(PitchBlack)
                        .border(1.dp, HairlineBorderLight)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = String.format(Locale.US, "%.1f %s · %.1fx WHO", driver.value, driver.unit, driver.whoRatio),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = EditorialBlue,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = driver.impactAssessment,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = InkMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Card 2: Cleanest Workout Window Card
        drivers.cleanestWindow?.let { window ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkElevated)
                    .border(1.dp, HairlineBorder)
                    .padding(14.dp)
            ) {
                // Category row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsRun,
                            contentDescription = null,
                            tint = window.aqiLevel.color,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "02 // OPTIMAL OUTDOOR WINDOW",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = window.aqiLevel.color,
                                letterSpacing = 0.8.sp,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Text(
                        text = window.dateLabel.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = InkFaint,
                            fontSize = 10.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Time & Projected AQI row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${window.startHour} — ${window.endHour}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = InkWhite,
                            fontSize = 18.sp,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Box(
                        modifier = Modifier
                            .background(PitchBlack)
                            .border(1.dp, window.aqiLevel.color)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "EST. AQI ${window.averageAqi.toInt()} · ${window.aqiLevel.label}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = window.aqiLevel.color,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = window.workoutRecommendation,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = InkMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Card 3: Wind & Atmospheric Dispersion Card
        drivers.windDispersion?.let { wind ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkElevated)
                    .border(1.dp, HairlineBorder)
                    .padding(14.dp)
            ) {
                // Category row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Air,
                            contentDescription = null,
                            tint = EditorialBlue,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "03 // WIND & DISPERSAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EditorialBlue,
                                letterSpacing = 0.8.sp,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(PitchBlack)
                            .border(1.dp, HairlineBorderLight)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = wind.stagnationRating,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = InkWhite,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Velocity & Telemetry row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = String.format(
                            Locale.US,
                            "%.1f km/h %s (%d°)",
                            wind.windSpeedKmh,
                            wind.windDirectionCompass,
                            wind.windDirectionDegrees.toInt()
                        ),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = InkWhite,
                            fontSize = 15.sp
                        )
                    )

                    Text(
                        text = String.format(
                            Locale.US,
                            "%.1f°C · %.0f%% HUMID",
                            wind.temperatureC,
                            wind.relativeHumidity
                        ),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = InkMuted,
                            fontSize = 10.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = wind.dispersionNote,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = InkMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                )
            }
        }
    }
}
