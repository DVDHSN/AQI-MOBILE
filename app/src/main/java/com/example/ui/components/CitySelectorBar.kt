package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LocationItem
import com.example.ui.animation.bounceClickable
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
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentPadding = PaddingValues(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Current GPS Location Pill
        item(key = "current_gps_pill") {
            val isCurrentActive = activeLocation.isCurrentLocation

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isCurrentActive) DarkElevated else DarkSurface)
                    .border(
                        1.dp,
                        if (isCurrentActive) EditorialBlue else HairlineBorder,
                        RoundedCornerShape(20.dp)
                    )
                    .bounceClickable(
                        enabled = !isLocating,
                        scaleDown = 0.94f
                    ) { onRequestCurrentLocation() }
                    .padding(horizontal = 11.dp, vertical = 6.dp)
                    .testTag("current_location_button"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (isLocating) {
                    CircularProgressIndicator(
                        color = EditorialBlue,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(12.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Detect current location",
                        tint = if (isCurrentActive) EditorialBlue else InkMuted,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Text(
                    text = if (isLocating) "Locating..." else "Current",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (isCurrentActive) InkWhite else InkMuted,
                        fontSize = 11.sp
                    )
                )
            }
        }

        // 2. Search Button Pill
        item(key = "search_city_pill") {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurface)
                    .border(1.dp, HairlineBorder, RoundedCornerShape(20.dp))
                    .bounceClickable(scaleDown = 0.94f) { onOpenSearch() }
                    .padding(horizontal = 11.dp, vertical = 6.dp)
                    .testTag("open_search_button"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search locations",
                    tint = InkMuted,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "Search",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = InkMuted,
                        fontSize = 11.sp
                    )
                )
            }
        }

        // 3. Location Chips (Saved & Curated Cities with animated scale microinteractions)
        items(locations, key = { it.id }) { loc ->
            val isSelected = loc.id == activeLocation.id

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) DarkElevated else DarkSurface)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) InkWhite.copy(alpha = 0.5f) else HairlineBorder,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .bounceClickable(scaleDown = 0.94f) { onSelectLocation(loc) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("location_chip_${loc.id}"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (loc.isSaved) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = if (isSelected) EditorialBlue else InkFaint,
                        modifier = Modifier.size(10.dp)
                    )
                }
                Text(
                    text = loc.name,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (isSelected) InkWhite else InkMuted,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
