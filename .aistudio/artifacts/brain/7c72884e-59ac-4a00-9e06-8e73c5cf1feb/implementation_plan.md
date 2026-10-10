# Atmosphere: Feature Expansion & Environmental Intelligence

A comprehensive enhancement to the Atmosphere Wired-inspired AQI tracker. This update incorporates 5-day predictive air quality forecasting, a 24-hour "Cleanest Window" workout and ventilation planner, a City-to-City Environmental Duel comparison mode, dominant pollutant driver analysis, and real-time wind/dispersion atmospheric telemetry—all sourced directly from Open-Meteo APIs.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural decisions were selected during clarification and guide this implementation:

- **Navigation & Mode Switch**: Top masthead mode tabs: `[ DISPATCH | CITY DUEL ]`. In "Dispatch" mode, the full single-city environmental dossier is displayed. In "City Duel" mode, users can pick any two saved/curated cities for a side-by-side editorial showdown and dual-trend overlay.
- **Timeline Projection Control**: Segmented timeline controls above the interactive trend chart: `[ 7-DAY HISTORY | 5-DAY FORECAST ]`. Allows seamless switching between 168 hours of historical trajectory and 120 hours of future atmospheric projections.
- **Atmospheric Drivers Strip**: Compact editorial cards positioned directly below the Hero AQI Card, featuring:
  - **Dominant Driver**: Pinpoints the pollutant contributing most to current health impact ($PM_{2.5}$, $O_3$, etc.) with WHO benchmark ratio.
  - **Cleanest Workout Window**: Evaluates the upcoming 24 hours to recommend the best time window for running and opening windows.
  - **Wind & Dispersion**: Live wind speed, direction, temperature, and atmospheric stagnation index explaining pollutant dispersal dynamics.
- **Zero Mock Data**: All metrics sourced from `https://air-quality-api.open-meteo.com`, `https://api.open-meteo.com`, and `https://geocoding-api.open-meteo.com`.

---

### 1. Overview & Core Concept

- **What It Does**: Expands Atmosphere from a retrospective tracker into a forward-looking environmental intelligence dashboard. Users can predict future air pollution spikes, optimize outdoor training schedules, compare living conditions across cities, and understand the meteorological drivers of air pollution.
- **Target Audience**: Runners, cyclists, parents, travelers, and urban residents who need to anticipate pollution shifts rather than just view historical readings.
- **Key Value**: Delivers proactive atmospheric forecasting and cross-city comparison without sacrificing the crisp, high-contrast Wired publication design system.

---

### 2. User Experience & Visual Design

#### New Screens & Components

```
┌──────────────────────────────────────────────────────────────┐
│                    ATMOSPHERE // DISPATCH                    │
│                 [ DISPATCH ]    [ CITY DUEL ]                │
│  ──────────────────────────────────────────────────────────  │
│  [ GPS ]  [ FIND CITY ]  [ TOKYO ★ ]  [ NEW YORK ]  [ LONDON ]│
│  ──────────────────────────────────────────────────────────  │
│  HERO AQI DISPATCH CARD: TOKYO, JAPAN                        │
│  48 MODERATE  ·  US AQI  ·  ZONE: ASIA/TOKYO                 │
│                                                              │
│  ATMOSPHERIC DRIVERS STRIP:                                  │
│  ┌─────────────────────────┐  ┌────────────────────────────┐ │
│  │ 01 // DOMINANT DRIVER   │  │ 02 // CLEANEST WINDOW      │ │
│  │ PM2.5 · 1.8x WHO GUIDE  │  │ 06:00 – 09:00 AM (AQI 32)  │ │
│  └─────────────────────────┘  └────────────────────────────┘ │
│  ┌─────────────────────────┐  ┌────────────────────────────┐ │
│  │ 03 // WIND & DISPERSION │  │ 04 // STAGNATION INDEX     │ │
│  │ 14 km/h NNE · 22°C      │  │ MODERATE DISPERSAL         │ │
│  └─────────────────────────┘  └────────────────────────────┘ │
│  ──────────────────────────────────────────────────────────  │
│  TIMELINE MODE:  [ 7-DAY HISTORY ]  [ 5-DAY FORECAST ]       │
│  METRIC: [ US AQI | PM2.5 | PM10 | O3 | NO2 | EUR AQI ]      │
│  ╭────────────────────────────────────────────────────────╮  │
│  │ Interactive Scrubbable Trend Chart (120h or 168h)      │  │
│  ╰────────────────────────────────────────────────────────╯  │
└──────────────────────────────────────────────────────────────┘
```

#### City Duel View
```
┌──────────────────────────────────────────────────────────────┐
│               ATMOSPHERE // ENVIRONMENTAL DUEL               │
│                 [ DISPATCH ]    [ CITY DUEL ]                │
│  ──────────────────────────────────────────────────────────  │
│  SELECT CITIES:                                              │
│  [ CITY A: TOKYO, JP ▼ ]     VS     [ CITY B: DELHI, IN ▼ ]  │
│                                                              │
│  ┌───────────────────────────┐  ┌──────────────────────────┐ │
│  │ TOKYO                     │  │ DELHI                    │ │
│  │ 42 GOOD                   │  │ 186 UNHEALTHY            │ │
│  │ Winner: Tokyo (+77% Clean)│  │ Primary: PM2.5 (9.2x WHO)│ │
│  └───────────────────────────┘  └──────────────────────────┘ │
│                                                              │
│  COMPARATIVE 7-DAY TREND OVERLAY:                            │
│  ╭────────────────────────────────────────────────────────╮  │
│  │ ── Tokyo (Cyan)    ┄┄ Delhi (Amber/Red)                │  │
│  │ Interactive dual scrubber comparing hourly variance    │  │
│  ╰────────────────────────────────────────────────────────╯  │
└──────────────────────────────────────────────────────────────┘
```

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1: Dual Timeline Query (Past 7 Days + Forecast 5 Days in Single Open-Meteo Call)**:
  - *Chosen Approach*: Query `past_days=7&forecast_days=5` in the air quality request.
  - *Why*: Retrieves both historical and forecast data in one network roundtrip, enabling instantaneous switching between History and Forecast tabs without network delays.
- **Decision 2: Open-Meteo Weather API Integration for Wind & Temperature**:
  - *Chosen Approach*: Add `WeatherApiService` querying `https://api.open-meteo.com/v1/forecast?current=temperature_2m,relative_humidity_2m,wind_speed_10m,wind_direction_10m`.
  - *Why*: Provides physical dispersion context (wind speed and direction) using the exact same Open-Meteo infrastructure.
- **Decision 3: In-Memory City Duel State**:
  - *Chosen Approach*: Maintain `duelCityA` and `duelCityB` in `AqiViewModel`, allowing quick selection from saved or curated cities with parallel fetching.

---

### 4. Technical Architecture & Data Strategy

```
┌─────────────────────────────────────────────────────────────┐
│                    Open-Meteo API Endpoints                 │
│  • air-quality-api.open-meteo.com (past_days=7, forecast=5) │
│  • api.open-meteo.com (wind_speed, wind_dir, temp, humidity)│
│  • geocoding-api.open-meteo.com (search)                    │
└──────────────┬──────────────────────────────┬───────────────┘
               │                              │
               ▼                              ▼
┌──────────────────────────────┐┌─────────────────────────────┐
│    AirQualityApiService      ││      WeatherApiService      │
└──────────────┬───────────────┘└─────────────┬───────────────┘
               │                              │
               ▼                              ▼
┌─────────────────────────────────────────────────────────────┐
│                   AirQualityRepository                      │
│   • Combines air quality history, forecast, and weather     │
│   • Computes: Dominant Pollutant Driver                     │
│   • Computes: Optimal 24h Cleanest Exercise Window          │
│   • Computes: Atmospheric Stagnation / Dispersal Rating     │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                   AqiViewModel & State                      │
│   • appMode: DISPATCH | DUEL                                │
│   • timelineMode: HISTORY (7d) | FORECAST (5d)              │
│   • duelReportA & duelReportB                               │
│   • atmosphericDrivers: DriverAnalysis                      │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                     Jetpack Compose UI                      │
│  ├── TopMasthead (with DISPATCH / DUEL mode tabs)            │
│  ├── CitySelectorBar (GPS, Find City, Pinned Tabs)           │
│  ├── AtmosphericDriversSection (Cleanest Window, Wind, Driver)│
│  ├── TrendChart (with History / Forecast toggle)            │
│  ├── CityDuelScreen (Head-to-head comparison view)          │
│  └── PollutantMatrix & HealthAdvisorySection                │
└─────────────────────────────────────────────────────────────┘
```

#### Verification Plan
- Unit & Robolectric tests verifying cleanest window calculation, dominant driver identification, forecast parsing, and City Duel data model.
- Applet compilation with `compile_applet`.
