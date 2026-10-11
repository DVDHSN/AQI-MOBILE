package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LocationItem
import com.example.ui.animation.bounceClick
import com.example.ui.icons.LucideIcons
import com.example.ui.theme.*

/**
 * City Selector bar mapped to Apple capsule pills.
 */
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
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // GPS Current Location Pill
        item(key = "current_gps_chip") {
            val isCurrent = activeLocation.isCurrentLocation
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(if (isCurrent) AppleActionBlue else AppleCardBackground)
                    .border(0.5.dp, AppleBorder, RoundedCornerShape(9999.dp))
                    .bounceClick { onRequestCurrentLocation() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("current_location_button"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isLocating) {
                    CircularProgressIndicator(
                        color = AppleTextPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(11.dp)
                    )
                } else {
                    Icon(
                        imageVector = LucideIcons.MapPin,
                        contentDescription = "Current GPS",
                        tint = AppleTextPrimary,
                        modifier = Modifier.size(11.dp)
                    )
                }
                Text(
                    text = if (isLocating) "Locating…" else "Current Location",
                    style = TypographyCaptionStrong.copy(
                        fontSize = 12.sp,
                        color = AppleTextPrimary
                    )
                )
            }
        }

        // Pinned stations
        items(locations, key = { it.id }) { loc ->
            val isSelected = !activeLocation.isCurrentLocation && loc.id == activeLocation.id
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(if (isSelected) AppleActionBlue else AppleCardBackground)
                    .border(0.5.dp, AppleBorder, RoundedCornerShape(9999.dp))
                    .bounceClick { onSelectLocation(loc) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("location_chip_${loc.id}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = loc.name,
                    style = TypographyCaptionStrong.copy(
                        fontSize = 12.sp,
                        color = if (isSelected) AppleTextPrimary else AppleTextSecondary
                    )
                )
            }
        }
    }
}
