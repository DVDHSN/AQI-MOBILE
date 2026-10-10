package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrentAirQuality
import com.example.ui.theme.*
import java.util.Locale

private data class PollutantCardItem(
    val formula: String,
    val name: String,
    val value: Float,
    val unit: String,
    val whoThreshold: Float,
    val impact: String
)

@Composable
fun PollutantMatrix(
    current: CurrentAirQuality,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        PollutantCardItem(
            formula = "PM2.5",
            name = "Fine Particulates",
            value = current.pm25,
            unit = "μg/m³",
            whoThreshold = 15f,
            impact = "Penetrates lung alveoli & enters bloodstream"
        ),
        PollutantCardItem(
            formula = "PM10",
            name = "Inhalable Dust",
            value = current.pm10,
            unit = "μg/m³",
            whoThreshold = 45f,
            impact = "Irritates upper respiratory tract & ocular tissues"
        ),
        PollutantCardItem(
            formula = "O₃",
            name = "Surface Ozone",
            value = current.ozone,
            unit = "μg/m³",
            whoThreshold = 100f,
            impact = "Aggravates asthma and decreases vital capacity"
        ),
        PollutantCardItem(
            formula = "NO₂",
            name = "Nitrogen Dioxide",
            value = current.nitrogenDioxide,
            unit = "μg/m³",
            whoThreshold = 25f,
            impact = "Combustion exhaust; inflames airways"
        ),
        PollutantCardItem(
            formula = "SO₂",
            name = "Sulphur Dioxide",
            value = current.sulphurDioxide,
            unit = "μg/m³",
            whoThreshold = 40f,
            impact = "Industrial gas; triggers bronchoconstriction"
        ),
        PollutantCardItem(
            formula = "CO",
            name = "Carbon Monoxide",
            value = current.carbonMonoxide,
            unit = "μg/m³",
            whoThreshold = 4000f,
            impact = "Reduces oxygen carrying capacity of hemoglobin"
        )
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .border(1.dp, HairlineBorder)
            .padding(16.dp)
            .testTag("pollutant_matrix_section")
    ) {
        // Section Header (Vertical stack - zero horizontal collision)
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ATMOSPHERIC COMPOSITION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp,
                        color = EditorialBlue,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = "WHO 24H GUIDELINES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = InkFaint,
                        fontSize = 9.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "CRITERIA POLLUTANTS",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = InkWhite,
                    letterSpacing = 0.5.sp,
                    fontSize = 15.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2-column Grid of Pollutant Cards
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { item ->
                        PollutantCard(
                            item = item,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun PollutantCard(
    item: PollutantCardItem,
    modifier: Modifier = Modifier
) {
    val ratio = if (item.whoThreshold > 0f) item.value / item.whoThreshold else 0f
    val statusColor = when {
        ratio <= 1.0f -> AqiGood
        ratio <= 2.0f -> AqiModerate
        ratio <= 3.5f -> AqiSensitive
        else -> AqiUnhealthy
    }

    Column(
        modifier = modifier
            .background(DarkElevated)
            .border(1.dp, HairlineBorder)
            .padding(12.dp)
            .testTag("pollutant_card_${item.formula}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.formula,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = InkWhite,
                    letterSpacing = 0.5.sp
                )
            )
            Text(
                text = String.format(Locale.US, "%.1fx WHO", ratio),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = statusColor,
                    fontSize = 10.sp
                )
            )
        }

        Text(
            text = item.name.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = InkFaint,
                fontSize = 9.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = String.format(Locale.US, "%.1f", item.value),
            style = MaterialTheme.typography.displaySmall.copy(
                color = InkWhite,
                fontSize = 22.sp
            )
        )
        Text(
            text = item.unit,
            style = MaterialTheme.typography.labelSmall.copy(
                color = InkMuted,
                fontSize = 10.sp
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Hairline severity indicator bar
        val progressFraction = (ratio / 3.0f).coerceIn(0.05f, 1f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(HairlineBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressFraction)
                    .fillMaxHeight()
                    .background(statusColor)
            )
        }
    }
}
