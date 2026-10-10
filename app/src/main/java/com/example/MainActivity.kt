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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            // Masthead with [ DISPATCH | CITY DUEL ] Mode Switch
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
                            .background(DarkElevated)
                            .border(1.dp, EditorialBlue)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
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
                    // Megacity & Saved Chips Bar with GPS Button
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
                                        text = "CONNECTING TO OPEN-METEO ATMOSPHERIC GRID...",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = InkMuted,
                                            letterSpacing = 1.sp
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
                                        .background(DarkSurface)
                                        .border(1.dp, HairlineBorder)
                                        .padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Text(
                                        text = "COMMUNICATION FAILURE // OPEN-METEO API",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = AqiUnhealthy,
                                            letterSpacing = 1.sp
                                        )
                                    )
                                    Text(
                                        text = uiState.errorMessage,
                                        style = MaterialTheme.typography.bodyMedium.copy(color = InkMuted)
                                    )
                                    Button(
                                        onClick = onRefresh,
                                        shape = MaterialTheme.shapes.extraSmall,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = InkWhite,
                                            contentColor = PitchBlack
                                        ),
                                        modifier = Modifier.testTag("retry_button")
                                    ) {
                                        Text(
                                            text = "RETRY TRANSMISSION",
                                            style = MaterialTheme.typography.labelLarge.copy(
                                                letterSpacing = 1.sp
                                            )
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
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // 1. Hero AQI Card with Save button & GPS status
                                item {
                                    HeroAqiCard(
                                        location = report.location,
                                        current = report.current,
                                        timezone = report.timezone,
                                        onToggleSaveLocation = onToggleSaveLocation
                                    )
                                }

                                // 2. Atmospheric Drivers Strip (Dominant Driver, Cleanest Window, Wind Dispersion)
                                report.drivers?.let { drivers ->
                                    item {
                                        AtmosphericDriversSection(drivers = drivers)
                                    }
                                }

                                // 3. Interactive Trend Chart (with 7-Day History & 5-Day Forecast toggle tabs)
                                item {
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

                                // 4. Criteria Pollutant Matrix
                                item {
                                    PollutantMatrix(current = report.current)
                                }

                                // 5. Actionable Health Guidelines
                                item {
                                    HealthAdvisorySection(level = report.current.level)
                                }

                                // 6. Editorial Footer Band
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(DarkSurface)
                                            .border(1.dp, HairlineBorder)
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = "ATMOSPHERE // DATA SPECIFICATION",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = InkFaint,
                                                letterSpacing = 1.sp
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "All meteorological and chemical dispersion readings are queried in real time from Open-Meteo European Centre for Medium-Range Weather Forecasts (ECMWF) and CAMS global atmospheric models. No synthetic or simulated figures are generated.",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = InkFaint,
                                                lineHeight = 18.sp
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
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
