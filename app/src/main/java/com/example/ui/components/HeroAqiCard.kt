package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrentAirQuality
import com.example.data.model.LocationItem
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun HeroAqiCard(
    location: LocationItem,
    current: CurrentAirQuality,
    timezone: String,
    onToggleSaveLocation: (LocationItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val level = current.level

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .border(1.dp, HairlineBorder)
            .padding(20.dp)
            .testTag("hero_aqi_card")
    ) {
        // Category Eyebrow & Badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f, fill = false),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "ATMOSPHERIC INDEX",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp,
                        color = InkFaint,
                        fontSize = 10.sp
                    ),
                    maxLines = 1
                )

                if (location.isCurrentLocation) {
                    Box(
                        modifier = Modifier
                            .background(PitchBlack)
                            .border(1.dp, EditorialBlue)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = null,
                                tint = EditorialBlue,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = "GPS ACTIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = EditorialBlue,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                        }
                    }
                }
            }

            // Save / Bookmark Location Button
            Row(
                modifier = Modifier
                    .border(1.dp, if (location.isSaved) EditorialBlue else HairlineBorderLight)
                    .background(PitchBlack)
                    .clickable { onToggleSaveLocation(location) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("toggle_save_location_button"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = if (location.isSaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = if (location.isSaved) "Remove from saved locations" else "Save location",
                    tint = if (location.isSaved) EditorialBlue else InkMuted,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = if (location.isSaved) "SAVED" else "SAVE CITY",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (location.isSaved) EditorialBlue else InkMuted,
                        fontSize = 10.sp,
                        letterSpacing = 0.8.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Headline City Name
        Text(
            text = "${location.name.uppercase()}, ${location.country.uppercase()}",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = InkWhite,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.testTag("hero_location_name")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = String.format(
                    Locale.US,
                    "LAT: %.2f° %s · LON: %.2f° %s",
                    kotlin.math.abs(location.latitude),
                    if (location.latitude >= 0) "N" else "S",
                    kotlin.math.abs(location.longitude),
                    if (location.longitude >= 0) "E" else "W"
                ),
                style = MaterialTheme.typography.labelSmall.copy(color = InkMuted)
            )

            Text(
                text = "ZONE: $timezone",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 0.6.sp,
                    color = InkFaint
                )
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Large AQI readout row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = current.usAqi.toInt().toString(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        color = InkWhite,
                        fontSize = 58.sp,
                        lineHeight = 58.sp
                    ),
                    modifier = Modifier.testTag("hero_aqi_value")
                )

                Column(modifier = Modifier.padding(bottom = 6.dp)) {
                    Text(
                        text = "US AQI",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = InkMuted,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = "EUR: ${current.europeanAqi.toInt()}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = InkFaint
                        )
                    )
                }
            }

            // Status Badge (Square editorial badge)
            Box(
                modifier = Modifier
                    .border(1.dp, level.color)
                    .background(PitchBlack)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("hero_aqi_status_badge")
            ) {
                Text(
                    text = level.label,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = level.color,
                        letterSpacing = 1.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hairline separator inside card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(HairlineBorder)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Editorial summary lead
        Text(
            text = level.detailedAdvisory,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = InkMuted,
                lineHeight = 22.sp
            )
        )
    }
}
