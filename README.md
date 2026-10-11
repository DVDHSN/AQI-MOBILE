# AQI — Minimalist Air Quality Intelligence

A minimalist luxury dark-mode Air Quality Index (AQI) intelligence application for Android built natively with **Kotlin** and **Jetpack Compose**, powered by real-time meteorological data from **Open-Meteo**.

Inspired by Apple Weather and Apple Health aesthetics, AQI blends high-contrast OLED black surfaces, tactile spring microinteractions, live atmospheric canvas animations, and scientific pollutant benchmarks.

---

## ✨ Features

- **Live Air Quality Index & Multi-Standard Scaling**:
  - Instant AQI readings with smooth count-up number animations.
  - One-tap switching between **US EPA** (0–500), **EUR CAQI** (1–5), and **WHO Guidelines** standards.
  - Continuous multi-stop spectrum gradient bar with spring-animated thumb indicator.
  - Ambient floating particle physics reacting to current air toxicity levels.

- **Precautionary Health Actions**:
  - Tailored health guidance categorized by activity and sensitivity.
  - Fluid scrolling chips for **Mask**, **Purifier**, **Windows**, **Exercise**, and **Sensitive Groups**.

- **Pollutant Breakdown Matrix**:
  - Detailed 2-column cards for **PM2.5**, **PM10**, **NO₂**, **SO₂**, **CO**, and **O₃** (Ground Ozone).
  - Animated progress bars benchmarked against official World Health Organization (WHO) exposure limits.

- **Interactive Atmospheric Trend & Predictive Outlook**:
  - Switch between **7-Day Historical Analysis** and **5-Day Predictive Outlook**.
  - Metric selector with smooth spring transitions (**US AQI**, **PM2.5**, **PM10**, **O₃**, **NO₂**, **EUR AQI**).
  - Interactive vector canvas chart with smooth curve entrance animations, touch scrubbing, and a breathing halo focus point.

- **Head-to-Head City Duel**:
  - Real-time side-by-side comparative analysis between any two saved or searched cities.
  - Animated AQI score delta readout and pollutant comparison breakdown.

- **30-Day Retrospective & Botanical Allergens**:
  - 30-day compliance calendar heatmap with clean days ratio.
  - Allergen and pollen monitoring (Birch, Grass, Alder, Ragweed, Olive) with risk severity tags.

- **Global Geocoding & GPS Location**:
  - Real-time device GPS location resolution.
  - Search any city globally with offline caching and pinned favorites.

- **Android Home Screen Widget**:
  - Glanceable AppWidget provider showing active location, numeric AQI, and colored severity badge.

---

## 🛠️ Technology Stack

| Layer | Technologies |
|---|---|
| **Language & Platform** | Kotlin 2.2.10, Android SDK 36 (Compile & Target) |
| **UI Framework** | Jetpack Compose, Material 3 (M3) |
| **Architecture** | MVVM (Model-View-ViewModel), Clean Architecture, StateFlow |
| **Networking** | Retrofit 2, OkHttp 3, Moshi (Kotlin Codegen) |
| **Iconography & Styling** | Custom Lucide & SF Symbols vector drawables, OLED Pure Black (`#000000`), System Gray 6 (`#1C1C1E`) |
| **Motion & Physics** | Compose Spring Physics (`animateFloatAsState`, `Animatable`, `infiniteRepeatable`), Touch Bounce Modifiers |
| **Testing** | JUnit 4, Robolectric, Kotlinx Coroutines Test |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Ladybug / Meerkat or command-line Android SDK 36
- JDK 21 (Temurin / OpenJDK)
- Gradle 9.3+

### Building the Project

Assemble the debug APK:

```bash
gradle assembleDebug
```

The compiled APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

### Running Tests

Execute local JVM unit and Robolectric tests:

```bash
gradle :app:testDebugUnitTest
```

---

## 📂 Project Structure

```
├── app/
│   ├── src/main/
│   │   ├── java/com/example/
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/
│   │   │   │   ├── model/         # Domain models (AqiLevel, AqiStandard, AirQualityReport)
│   │   │   │   ├── network/       # Retrofit Open-Meteo API interfaces
│   │   │   │   ├── repository/    # AirQualityRepository with in-memory caching
│   │   │   │   └── storage/       # SharedPreferences for saved cities
│   │   │   ├── ui/
│   │   │   │   ├── animation/     # Spring bounce modifiers & atmospheric particle physics
│   │   │   │   ├── components/    # Hero card, TrendChart, CityDuel, Pollutant grid, Heatmap
│   │   │   │   ├── icons/         # Lucide hairline vector icon suite
│   │   │   │   ├── theme/         # Color tokens, Typography, Dark Theme
│   │   │   │   └── viewmodel/     # AqiViewModel & AqiUiState
│   │   │   └── widget/            # Android Home Screen AppWidgetProvider
│   │   └── res/                   # Drawables, mipmaps, launcher icons, layout resources
│   └── build.gradle.kts
├── metadata.json
├── settings.gradle.kts
└── README.md
```

---

## 📄 License

Open-source under the Apache License 2.0. Meteorological data provided by [Open-Meteo](https://open-meteo.com/).
