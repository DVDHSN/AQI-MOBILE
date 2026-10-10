package com.example

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AppMode
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.AqiUiState
import com.example.ui.viewmodel.AqiViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtmosphereTheme {
                val context = LocalContext.current
                val application = context.applicationContext as Application
                val viewModel: AqiViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return AqiViewModel(application) as T
                        }
                    }
                )
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                // Runtime permission launcher for GPS access
                val locationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                    val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                    if (fineGranted || coarseGranted) {
                        viewModel.fetchCurrentLocation()
                    }
                }

                val onGpsClick = {
                    val fineGranted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    val coarseGranted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                    if (fineGranted || coarseGranted) {
                        viewModel.fetchCurrentLocation()
                    } else {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = PitchBlack,
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    AtmosphereAppContent(
                        uiState = uiState,
                        onRefresh = viewModel::refresh,
                        onSelectAppMode = viewModel::setAppMode,
                        onSelectTimelineMode = viewModel::setTimelineMode,
                        onSelectLocation = viewModel::selectLocation,
                        onToggleSaveLocation = viewModel::toggleSaveLocation,
                        onRequestCurrentLocation = onGpsClick,
                        onOpenSearch = { viewModel.setSearchSheetOpen(true) },
                        onSelectMetric = viewModel::selectMetric,
                        onCycleStandard = viewModel::cycleAqiStandard,
                        onScrubPoint = viewModel::onScrubPoint,
                        onSelectDuelCityA = viewModel::selectDuelCityA,
                        onSelectDuelCityB = viewModel::selectDuelCityB,
                        onSearchQueryChanged = viewModel::onSearchQueryChanged,
                        onSelectSearchResult = viewModel::selectSearchResult,
                        onCloseSearch = { viewModel.setSearchSheetOpen(false) },
                        onDismissFeedback = viewModel::dismissFeedback,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AtmosphereAppContent(
    uiState: AqiUiState,
    onRefresh: () -> Unit,
    onSelectAppMode: (AppMode) -> Unit,
    onSelectTimelineMode: (com.example.data.model.TimelineMode) -> Unit,
    onSelectLocation: (com.example.data.model.LocationItem) -> Unit,
    onToggleSaveLocation: (com.example.data.model.LocationItem) -> Unit,
    onRequestCurrentLocation: () -> Unit,
    onOpenSearch: () -> Unit,
    onSelectMetric: (com.example.data.model.ChartMetric) -> Unit,
    onCycleStandard: () -> Unit,
    onScrubPoint: (com.example.data.model.HourlyDataPoint?) -> Unit,
    onSelectDuelCityA: (com.example.data.model.LocationItem) -> Unit,
    onSelectDuelCityB: (com.example.data.model.LocationItem) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSelectSearchResult: (com.example.data.model.LocationItem) -> Unit,
    onCloseSearch: () -> Unit,
    onDismissFeedback: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Masthead with Overview / Compare switcher
            TopMasthead(
                appMode = uiState.appMode,
                onSelectAppMode = onSelectAppMode,
                isRefreshing = uiState.isRefreshing,
                onRefresh = onRefresh
            )

            // Feedback Banner (e.g. GPS detected or saved notification)
            AnimatedVisibility(visible = uiState.locationFeedbackMessage != null) {
                uiState.locationFeedbackMessage?.let { msg ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkElevated)
                            .border(1.dp, HairlineBorderLight, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = InkWhite,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss notice",
                            tint = InkMuted,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { onDismissFeedback() }
                        )
                    }
                }
            }

            // Loading / Locating line indicator
            AnimatedVisibility(visible = uiState.isLoading || uiState.isRefreshing || uiState.isLocating) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                    color = EditorialBlue,
                    trackColor = PitchBlack
                )
            }

            // Main View Switcher: DISPATCH vs CITY DUEL
            when (uiState.appMode) {
                AppMode.DUEL -> {
                    CityDuelScreen(
                        availableLocations = uiState.pinnedLocations,
                        cityA = uiState.duelCityA,
                        cityB = uiState.duelCityB,
                        reportA = uiState.duelReportA,
                        reportB = uiState.duelReportB,
                        isLoadingA = uiState.isLoadingDuelA,
                        isLoadingB = uiState.isLoadingDuelB,
                        onSelectCityA = onSelectDuelCityA,
                        onSelectCityB = onSelectDuelCityB,
                        selectedMetric = uiState.selectedMetric,
                        onSelectMetric = onSelectMetric,
                        modifier = Modifier.weight(1f)
                    )
                }

                AppMode.DISPATCH -> {
                    // Location Pill Chips Bar with GPS & Search
                    CitySelectorBar(
                        locations = uiState.pinnedLocations,
                        activeLocation = uiState.activeLocation,
                        isLocating = uiState.isLocating,
                        onSelectLocation = onSelectLocation,
                        onRequestCurrentLocation = onRequestCurrentLocation,
                        onOpenSearch = onOpenSearch
                    )

                    // Body Area
                    when {
                        uiState.isLoading && uiState.report == null -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    CircularProgressIndicator(
                                        color = EditorialBlue,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Text(
                                        text = "Loading air quality...",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = InkMuted,
                                            fontSize = 13.sp
                                        )
                                    )
                                }
                            }
                        }

                        uiState.errorMessage != null && uiState.report == null -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(DarkSurface)
                                        .border(1.dp, HairlineBorder, RoundedCornerShape(20.dp))
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Text(
                                        text = "Unable to Load Data",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = InkWhite,
                                            fontSize = 16.sp
                                        )
                                    )
                                    Text(
                                        text = uiState.errorMessage,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = InkMuted,
                                            fontSize = 13.sp
                                        )
                                    )
                                    Button(
                                        onClick = onRefresh,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = InkWhite,
                                            contentColor = PitchBlack
                                        ),
                                        modifier = Modifier.testTag("retry_button")
                                    ) {
                                        Text(
                                            text = "Retry",
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                }
                            }
                        }

                        uiState.report != null -> {
                            val report = uiState.report
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("main_content_list"),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // 1. Unified Hero AQI Card with Direct Tap Standards Switcher
                                item(key = "hero_aqi_item") {
                                    HeroAqiCard(
                                        location = report.location,
                                        current = report.current,
                                        standard = uiState.aqiStandard,
                                        drivers = report.drivers,
                                        onToggleSaveLocation = onToggleSaveLocation,
                                        onCycleStandard = onCycleStandard
                                    )
                                }

                                // 2. Pollen & Seasonal Allergen Index Card
                                report.allergenReport?.let { allergen ->
                                    item(key = "pollen_allergen_item") {
                                        PollenAllergenCard(report = allergen)
                                    }
                                }

                                // 3. Interactive Trend Chart (with 7-Day History & 5-Day Forecast)
                                item(key = "trend_chart_item") {
                                    TrendChart(
                                        historyData = report.hourlyHistory,
                                        forecastData = report.hourlyForecast,
                                        timelineMode = uiState.timelineMode,
                                        onSelectTimelineMode = onSelectTimelineMode,
                                        selectedMetric = uiState.selectedMetric,
                                        onSelectMetric = onSelectMetric,
                                        scrubbedPoint = uiState.scrubbedPoint,
                                        onScrubPoint = onScrubPoint
                                    )
                                }

                                // 4. 30-Day Air Quality Calendar Heatmap (Expandable directly below Trend Chart)
                                report.heatmapReport?.let { heatmap ->
                                    item(key = "monthly_heatmap_item") {
                                        MonthlyHeatmapCard(report = heatmap)
                                    }
                                }

                                // 5. Compact Pollutant Matrix
                                item(key = "pollutant_matrix_item") {
                                    PollutantMatrix(current = report.current)
                                }

                                // 6. Actionable Health Guidelines
                                item(key = "health_advisory_item") {
                                    HealthAdvisorySection(level = report.current.level)
                                }

                                // Bottom breathing spacer
                                item(key = "bottom_spacer_item") {
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Global Geocoding Search Bottom Sheet
        CitySearchSheet(
            isOpen = uiState.isSearchSheetOpen,
            query = uiState.searchQuery,
            onQueryChanged = onSearchQueryChanged,
            searchResults = uiState.searchResults,
            isSearching = uiState.isSearching,
            onSelectLocation = onSelectSearchResult,
            onToggleSaveLocation = onToggleSaveLocation,
            onDismiss = onCloseSearch
        )
    }
}
