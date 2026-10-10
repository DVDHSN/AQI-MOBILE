package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LocationItem
import com.example.ui.theme.*

@Composable
fun CitySelectorBar(
    locations: List<LocationItem>,
    activeLocation: LocationItem,
    isLocating: Boolean,
    onSelectLocation: (LocationItem) -> Unit,
    onRequestCurrentLocation: () -> Unit,
    onOpenSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(PitchBlack)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Current GPS Location Button
            item {
                Row(
                    modifier = Modifier
                        .height(36.dp)
                        .border(
                            1.dp,
                            if (activeLocation.isCurrentLocation) EditorialBlue else HairlineBorderLight
                        )
                        .background(if (activeLocation.isCurrentLocation) DarkElevated else PitchBlack)
                        .clickable(enabled = !isLocating) { onRequestCurrentLocation() }
                        .padding(horizontal = 12.dp)
                        .testTag("current_location_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (isLocating) {
                        CircularProgressIndicator(
                            color = EditorialBlue,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Detect current GPS location",
                            tint = if (activeLocation.isCurrentLocation) EditorialBlue else InkWhite,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = if (isLocating) "LOCATING..." else "GPS",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = if (activeLocation.isCurrentLocation) EditorialBlue else InkWhite,
                            letterSpacing = 0.8.sp
                        )
                    )
                }
            }

            // 2. Search Button
            item {
                Row(
                    modifier = Modifier
                        .height(36.dp)
                        .border(1.dp, HairlineBorderLight)
                        .background(DarkSurface)
                        .clickable { onOpenSearch() }
                        .padding(horizontal = 12.dp)
                        .testTag("open_search_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search global locations",
                        tint = InkMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "FIND CITY",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = InkMuted,
                            letterSpacing = 0.8.sp
                        )
                    )
                }
            }

            // 3. Location Tabs (Saved & Curated)
            items(locations, key = { it.id }) { loc ->
                val isSelected = loc.id == activeLocation.id
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .background(if (isSelected) InkWhite else PitchBlack)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) InkWhite else HairlineBorder
                        )
                        .clickable { onSelectLocation(loc) }
                        .padding(horizontal = 12.dp)
                        .testTag("location_chip_${loc.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (loc.isCurrentLocation) {
                            Text(
                                text = "📍",
                                fontSize = 11.sp
                            )
                        } else if (loc.isSaved) {
                            Text(
                                text = "★",
                                color = if (isSelected) PitchBlack else EditorialBlue,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = loc.name.uppercase(),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = if (isSelected) PitchBlack else InkMuted,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
            }
        }

        // Hairline divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(HairlineBorder)
        )
    }
}
