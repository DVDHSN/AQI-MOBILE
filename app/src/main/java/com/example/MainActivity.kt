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
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AppMode
import com.example.data.model.ChartMetric
import com.example.data.model.HourlyDataPoint
import com.example.data.model.LocationItem
import com.example.data.model.TimelineMode
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
                    containerColor = SurfaceBlack,
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
    onSelectTimelineMode: (TimelineMode) -> Unit,
    onSelectLocation: (LocationItem) -> Unit,
    onToggleSaveLocation: (LocationItem) -> Unit,
    onRequestCurrentLocation: () -> Unit,
    onOpenSearch: () -> Unit,
    onSelectMetric: (ChartMetric) -> Unit,
    onCycleStandard: () -> Unit,
    onScrubPoint: (HourlyDataPoint?) -> Unit,
    onSelectDuelCityA: (LocationItem) -> Unit,
    onSelectDuelCityB: (LocationItem) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onSelectSearchResult: (LocationItem) -> Unit,
    onCloseSearch: () -> Unit,
    onDismissFeedback: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceBlack)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Attached Classic Apple Top Navigation Bar with sliding segmented control
            ClassicAppleNavBar(
                appMode = uiState.appMode,
                onSelectAppMode = onSelectAppMode,
                activeLocation = uiState.activeLocation,
                isRefreshing = uiState.isRefreshing,
                isLocating = uiState.isLocating,
                onRefresh = onRefresh,
                onRequestCurrentLocation = onRequestCurrentLocation,
                onOpenSearch = onOpenSearch
            )

            // 2. Animated transition between Overview and Compare modes
            AnimatedContent(
                targetState = uiState.appMode,
                transitionSpec = {
                    if (targetState == AppMode.DUEL) {
                        slideInHorizontally(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) { width -> width / 3 } + fadeIn(animationSpec = tween(200)) togetherWith
                            slideOutHorizontally(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ) { width -> -width / 3 } + fadeOut(animationSpec = tween(160))
                    } else {
                        slideInHorizontally(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) { width -> -width / 3 } + fadeIn(animationSpec = tween(200)) togetherWith
                            slideOutHorizontally(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ) { width -> width / 3 } + fadeOut(animationSpec = tween(160))
                    }
                },
                modifier = Modifier.weight(1f),
                label = "appleModeSwitchTransition"
            ) { mode ->
                when (mode) {
                    AppMode.DISPATCH -> {
                        AQIDashboardScreen(
                            uiState = uiState,
                            onRefresh = onRefresh,
                            onSelectAppMode = onSelectAppMode,
                            onSelectTimelineMode = onSelectTimelineMode,
                            onSelectLocation = onSelectLocation,
                            onToggleSaveLocation = onToggleSaveLocation,
                            onRequestCurrentLocation = onRequestCurrentLocation,
                            onOpenSearch = onOpenSearch,
                            onSelectMetric = onSelectMetric,
                            onCycleStandard = onCycleStandard,
                            onScrubPoint = onScrubPoint,
                            onDismissFeedback = onDismissFeedback,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

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
                            onBackToOverview = { onSelectAppMode(AppMode.DISPATCH) },
                            modifier = Modifier.fillMaxSize()
                        )
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
