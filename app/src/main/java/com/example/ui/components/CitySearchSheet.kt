package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LocationItem
import com.example.ui.theme.*
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySearchSheet(
    isOpen: Boolean,
    query: String,
    onQueryChanged: (String) -> Unit,
    searchResults: List<LocationItem>,
    isSearching: Boolean,
    onSelectLocation: (LocationItem) -> Unit,
    onToggleSaveLocation: (LocationItem) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = PitchBlack,
        dragHandle = null,
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(16.dp)
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GLOBAL GEOCODING DIRECTORY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            color = InkFaint
                        )
                    )
                    Text(
                        text = "FIND ANY GLOBAL LOCATION",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = InkWhite,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close search",
                        tint = InkWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search input field with square borders
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .border(1.dp, EditorialBlue)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = EditorialBlue,
                    modifier = Modifier.size(18.dp)
                )

                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Search city (e.g. Madrid, Seoul, Chicago)...",
                            style = MaterialTheme.typography.bodyLarge.copy(color = InkFaint)
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChanged,
                        singleLine = true,
                        cursorBrush = SolidColor(InkWhite),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(color = InkWhite),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_text_input")
                    )
                }

                if (query.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                        tint = InkMuted,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { onQueryChanged("") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search status indicator
            if (isSearching) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    color = EditorialBlue,
                    trackColor = DarkSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Results List
            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = if (query.length < 2) {
                            "Type at least 2 letters to query Open-Meteo's geocoding database."
                        } else if (!isSearching) {
                            "No global locations matching \"$query\" found."
                        } else {
                            "Searching coordinates..."
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(color = InkFaint)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(searchResults, key = { it.id }) { loc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarkElevated)
                                .border(1.dp, HairlineBorder)
                                .clickable { onSelectLocation(loc) }
                                .padding(14.dp)
                                .testTag("search_result_${loc.id}"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = loc.name.uppercase(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = InkWhite,
                                        letterSpacing = 0.5.sp
                                    )
                                )

                                Text(
                                    text = listOfNotNull(loc.region, loc.country).joinToString(", "),
                                    style = MaterialTheme.typography.bodyMedium.copy(color = InkMuted)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = String.format(
                                        Locale.US,
                                        "LAT: %.2f° · LON: %.2f°",
                                        loc.latitude,
                                        loc.longitude
                                    ),
                                    style = MaterialTheme.typography.labelSmall.copy(color = InkFaint)
                                )
                            }

                            // Quick Bookmark button
                            IconButton(
                                onClick = { onToggleSaveLocation(loc) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("search_bookmark_${loc.id}")
                            ) {
                                Icon(
                                    imageVector = if (loc.isSaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = if (loc.isSaved) "Remove saved location" else "Save location",
                                    tint = if (loc.isSaved) EditorialBlue else InkMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
