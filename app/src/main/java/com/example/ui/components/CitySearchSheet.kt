package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LocationItem
import com.example.ui.theme.*

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
        containerColor = DarkSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(HairlineBorderLight)
            )
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Find City",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = InkWhite,
                        fontSize = 20.sp
                    )
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close search",
                        tint = InkMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search input field with rounded border
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkElevated)
                    .border(1.dp, HairlineBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp),
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
                            text = "Search city or country...",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = InkFaint,
                                fontSize = 14.sp
                            )
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChanged,
                        singleLine = true,
                        cursorBrush = SolidColor(InkWhite),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = InkWhite,
                            fontSize = 14.sp
                        ),
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

            // Search progress indicator
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
                            "Type at least 2 letters to search locations."
                        } else if (!isSearching) {
                            "No cities matching \"$query\" found."
                        } else {
                            "Searching..."
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = InkFaint,
                            fontSize = 13.sp
                        )
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
                                .clip(RoundedCornerShape(14.dp))
                                .background(DarkElevated)
                                .clickable { onSelectLocation(loc) }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .testTag("search_result_${loc.id}"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = loc.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = InkWhite,
                                        fontSize = 15.sp
                                    )
                                )

                                val subtext = listOfNotNull(loc.region, loc.country).joinToString(", ")
                                if (subtext.isNotBlank()) {
                                    Text(
                                        text = subtext,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = InkMuted,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
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
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
