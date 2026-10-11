package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import com.example.ui.animation.bounceClick
import com.example.ui.icons.LucideIcons
import com.example.ui.theme.*

/**
 * City Search Modal Sheet designed with Apple minimalism.
 * Full-pill search field, crisp Lucide hairline icons, dark elevated cards, and tactile spring feedback.
 */
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
        containerColor = AppleCardBackground,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(AppleCardSecondary)
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
                    text = "Search Cities",
                    style = TypographyDisplayMd.copy(fontSize = 22.sp)
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AppleCardSecondary)
                        .bounceClick { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = LucideIcons.X,
                        contentDescription = "Close search",
                        tint = AppleTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Capsule search input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(9999.dp))
                    .background(AppleCardSecondary)
                    .border(0.5.dp, AppleBorder, RoundedCornerShape(9999.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = LucideIcons.Search,
                    contentDescription = null,
                    tint = AppleTextSecondary,
                    modifier = Modifier.size(16.dp)
                )

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChanged,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_text_input"),
                    textStyle = TypographyBody.copy(
                        color = AppleTextPrimary,
                        fontSize = 15.sp
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(AppleActionBlue),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text(
                                text = "Search worldwide cities…",
                                style = TypographyBody.copy(
                                    color = AppleTextSecondary,
                                    fontSize = 15.sp
                                )
                            )
                        }
                        innerTextField()
                    }
                )

                if (query.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(AppleBorder)
                            .bounceClick { onQueryChanged("") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = LucideIcons.X,
                            contentDescription = "Clear search",
                            tint = AppleTextPrimary,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Results List
            when {
                isSearching -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = AppleActionBlue,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                searchResults.isEmpty() && query.isNotBlank() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No cities found for \"$query\"",
                            style = TypographyBody.copy(color = AppleTextSecondary, fontSize = 14.sp)
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(searchResults, key = { it.id }) { loc ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(AppleCardSecondary)
                                    .border(0.5.dp, AppleBorder, RoundedCornerShape(14.dp))
                                    .bounceClick {
                                        onSelectLocation(loc)
                                        onDismiss()
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                                    .testTag("search_result_item_${loc.id}"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = loc.name,
                                        style = TypographyBodyStrong.copy(fontSize = 15.sp, color = AppleTextPrimary)
                                    )
                                    Text(
                                        text = loc.displayName,
                                        style = TypographyFinePrint.copy(color = AppleTextSecondary)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(AppleCardBackground)
                                        .bounceClick { onToggleSaveLocation(loc) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (loc.isSaved) LucideIcons.BookmarkFilled else LucideIcons.Bookmark,
                                        contentDescription = if (loc.isSaved) "Remove" else "Save",
                                        tint = if (loc.isSaved) AppleActionBlue else AppleTextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
