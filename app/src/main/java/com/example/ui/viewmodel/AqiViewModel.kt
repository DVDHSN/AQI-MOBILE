package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.location.LocationTracker
import com.example.data.model.*
import com.example.data.repository.AirQualityRepository
import com.example.data.storage.SavedLocationsPreferences
import com.example.widget.AqiAppWidgetProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AqiUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLocating: Boolean = false,
    val report: AirQualityReport? = null,
    val errorMessage: String? = null,
    val locationFeedbackMessage: String? = null,
    val activeLocation: LocationItem,
    val savedLocations: List<LocationItem> = emptyList(),
    val pinnedLocations: List<LocationItem> = emptyList(),
    val selectedMetric: ChartMetric = ChartMetric.US_AQI,
    val aqiStandard: AqiStandard = AqiStandard.US_EPA,
    val scrubbedPoint: HourlyDataPoint? = null,
    val isSearchSheetOpen: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<LocationItem> = emptyList(),
    val isSearching: Boolean = false,
    // Feature States
    val appMode: AppMode = AppMode.DISPATCH,
    val timelineMode: TimelineMode = TimelineMode.PAST_24_HOURS,
    val duelCityA: LocationItem,
    val duelCityB: LocationItem,
    val duelReportA: AirQualityReport? = null,
    val duelReportB: AirQualityReport? = null,
    val isLoadingDuelA: Boolean = false,
    val isLoadingDuelB: Boolean = false
)

class AqiViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: AirQualityRepository = AirQualityRepository()
) : AndroidViewModel(application) {

    private val preferences = SavedLocationsPreferences(application)
    private val locationTracker = LocationTracker(application)

    private val initialSaved = preferences.getSavedLocations()
    private val defaultLocation = initialSaved.firstOrNull() ?: repository.curatedMegacities.first()
    private val defaultCityB = repository.curatedMegacities.getOrNull(3) ?: repository.curatedMegacities.last()

    private val _uiState = MutableStateFlow(
        AqiUiState(
            activeLocation = defaultLocation.copy(isSaved = preferences.isLocationSaved(defaultLocation.id)),
            savedLocations = initialSaved,
            pinnedLocations = buildCombinedRoster(initialSaved, repository.curatedMegacities),
            duelCityA = defaultLocation,
            duelCityB = defaultCityB
        )
    )
    val uiState: StateFlow<AqiUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadAirQuality(defaultLocation)
    }

    private fun buildCombinedRoster(
        saved: List<LocationItem>,
        curated: List<LocationItem>
    ): List<LocationItem> {
        val list = mutableListOf<LocationItem>()
        list.addAll(saved.map { it.copy(isSaved = true) })
        curated.forEach { c ->
            if (saved.none { it.id == c.id }) {
                list.add(c.copy(isSaved = false))
            }
        }
        return list
    }

    fun setAppMode(mode: AppMode) {
        _uiState.update { it.copy(appMode = mode) }
        if (mode == AppMode.DUEL) {
            if (_uiState.value.duelReportA == null) {
                loadDuelCityA(_uiState.value.duelCityA)
            }
            if (_uiState.value.duelReportB == null) {
                loadDuelCityB(_uiState.value.duelCityB)
            }
        }
    }

    fun setTimelineMode(mode: TimelineMode) {
        _uiState.update { it.copy(timelineMode = mode, scrubbedPoint = null) }
    }

    fun cycleAqiStandard() {
        val nextStandard = when (_uiState.value.aqiStandard) {
            AqiStandard.US_EPA -> AqiStandard.EUROPEAN
            AqiStandard.EUROPEAN -> AqiStandard.WHO
            AqiStandard.WHO -> AqiStandard.US_EPA
        }
        _uiState.update { it.copy(aqiStandard = nextStandard) }
    }

    fun selectDuelCityA(location: LocationItem) {
        _uiState.update { it.copy(duelCityA = location) }
        loadDuelCityA(location)
    }

    fun selectDuelCityB(location: LocationItem) {
        _uiState.update { it.copy(duelCityB = location) }
        loadDuelCityB(location)
    }

    private fun loadDuelCityA(location: LocationItem) {
        _uiState.update { it.copy(isLoadingDuelA = true) }
        viewModelScope.launch {
            val result = repository.fetchAirQuality(location)
            _uiState.update { state ->
                state.copy(
                    isLoadingDuelA = false,
                    duelReportA = result.getOrNull()
                )
            }
        }
    }

    private fun loadDuelCityB(location: LocationItem) {
        _uiState.update { it.copy(isLoadingDuelB = true) }
        viewModelScope.launch {
            val result = repository.fetchAirQuality(location)
            _uiState.update { state ->
                state.copy(
                    isLoadingDuelB = false,
                    duelReportB = result.getOrNull()
                )
            }
        }
    }

    fun selectLocation(location: LocationItem) {
        val isSaved = preferences.isLocationSaved(location.id)
        val target = location.copy(isSaved = isSaved)
        if (_uiState.value.activeLocation.id == target.id && _uiState.value.report != null) return
        _uiState.update { it.copy(activeLocation = target, scrubbedPoint = null, locationFeedbackMessage = null) }
        loadAirQuality(target)
    }

    fun toggleSaveLocation(location: LocationItem) {
        val currentlySaved = preferences.isLocationSaved(location.id)
        val updatedSaved = if (currentlySaved) {
            preferences.removeSavedLocation(location.id)
        } else {
            preferences.saveLocation(location)
        }

        val updatedPinned = buildCombinedRoster(updatedSaved, repository.curatedMegacities)
        val activeIsThis = _uiState.value.activeLocation.id == location.id
        val newActive = if (activeIsThis) {
            _uiState.value.activeLocation.copy(isSaved = !currentlySaved)
        } else {
            _uiState.value.activeLocation
        }

        _uiState.update { state ->
            state.copy(
                savedLocations = updatedSaved,
                pinnedLocations = updatedPinned,
                activeLocation = newActive,
                locationFeedbackMessage = if (!currentlySaved) {
                    "Location '${location.name}' saved to roster"
                } else {
                    "Location '${location.name}' removed from saved"
                }
            )
        }
    }

    fun fetchCurrentLocation() {
        _uiState.update { it.copy(isLocating = true, locationFeedbackMessage = null) }
        viewModelScope.launch {
            val locationItem = locationTracker.getCurrentLocationItem()
            if (locationItem != null) {
                val isSaved = preferences.isLocationSaved(locationItem.id)
                val target = locationItem.copy(isSaved = isSaved)

                val currentPinned = _uiState.value.pinnedLocations.toMutableList()
                currentPinned.removeAll { it.isCurrentLocation }
                currentPinned.add(0, target)

                _uiState.update {
                    it.copy(
                        isLocating = false,
                        activeLocation = target,
                        pinnedLocations = currentPinned,
                        locationFeedbackMessage = "Detected location: ${target.name}",
                        scrubbedPoint = null
                    )
                }
                loadAirQuality(target)
            } else {
                _uiState.update {
                    it.copy(
                        isLocating = false,
                        locationFeedbackMessage = "Unable to resolve GPS position. Please ensure location services are enabled."
                    )
                }
            }
        }
    }

    fun dismissFeedback() {
        _uiState.update { it.copy(locationFeedbackMessage = null) }
    }

    fun refresh() {
        val currentLoc = _uiState.value.activeLocation
        _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
        viewModelScope.launch {
            val result = repository.fetchAirQuality(currentLoc, forceRefresh = true)
            _uiState.update { state ->
                result.fold(
                    onSuccess = { report ->
                        updateWidget(report)
                        state.copy(
                            report = report,
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { err ->
                        state.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = err.localizedMessage ?: "Failed to refresh atmospheric data"
                        )
                    }
                )
            }
        }
    }

    fun selectMetric(metric: ChartMetric) {
        _uiState.update { it.copy(selectedMetric = metric) }
    }

    fun onScrubPoint(point: HourlyDataPoint?) {
        _uiState.update { it.copy(scrubbedPoint = point) }
    }

    fun setSearchSheetOpen(open: Boolean) {
        _uiState.update {
            it.copy(
                isSearchSheetOpen = open,
                searchQuery = if (!open) "" else it.searchQuery,
                searchResults = if (!open) emptyList() else it.searchResults
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        if (query.trim().length < 2) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(300)
            _uiState.update { it.copy(isSearching = true) }
            val result = repository.searchLocations(query)
            _uiState.update { state ->
                state.copy(
                    isSearching = false,
                    searchResults = result.getOrDefault(emptyList()).map { item ->
                        item.copy(isSaved = preferences.isLocationSaved(item.id))
                    }
                )
            }
        }
    }

    fun selectSearchResult(location: LocationItem) {
        val isSaved = preferences.isLocationSaved(location.id)
        val target = location.copy(isSaved = isSaved)
        val currentPinned = _uiState.value.pinnedLocations.toMutableList()
        if (currentPinned.none { it.id == target.id }) {
            currentPinned.add(0, target)
        }
        _uiState.update {
            it.copy(
                pinnedLocations = currentPinned,
                activeLocation = target,
                isSearchSheetOpen = false,
                searchQuery = "",
                searchResults = emptyList(),
                scrubbedPoint = null
            )
        }
        loadAirQuality(target)
    }

    private fun loadAirQuality(location: LocationItem) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = repository.fetchAirQuality(location)
            _uiState.update { state ->
                result.fold(
                    onSuccess = { report ->
                        updateWidget(report)
                        state.copy(
                            report = report,
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { err ->
                        state.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = err.localizedMessage ?: "Failed to connect to Open-Meteo"
                        )
                    }
                )
            }
        }
    }

    private fun updateWidget(report: AirQualityReport) {
        try {
            val aqi = report.current.usAqi.toInt()
            val bestWindow = report.drivers?.cleanestWindow?.let {
                "Best window: ${it.startHour} - ${it.endHour}"
            } ?: "Air conditions updated"
            AqiAppWidgetProvider.pushWidgetUpdate(
                context = getApplication(),
                locationName = report.location.name,
                aqi = aqi,
                level = report.current.level,
                bestWindowText = bestWindow
            )
        } catch (_: Exception) {
            // Widget update failure is non-fatal
        }
    }
}
