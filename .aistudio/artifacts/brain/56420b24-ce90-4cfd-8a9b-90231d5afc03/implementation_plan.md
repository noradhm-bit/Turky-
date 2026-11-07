# منارة الإسلام (Manarat Al-Islam) - Comprehensive Islamic Companion

A comprehensive, customizable Islamic Android application delivering astronomical prayer times, personalized adhan and pre-prayer alarms, rich visual popups, dual home screen widgets, lightweight background battery-conscious alarm scheduling with boot auto-start, Holy Quran reader with large font and audio, authentic Hadith and Seerah libraries, and a dedicated Ramadan & Eid suite.

## User Review & Critical Decisions

> [!IMPORTANT]
> The following design and architectural choices were confirmed during Phase 1:
> - **Dual Home Screen Widgets**: Both a full prayer times countdown widget (inspired by Prayer Now & Al-Moazin) and a daily rotating Athkar & Sebha widget (inspired by Athkar app) will be provided.
> - **Dual Alert System in Background**: Prayer times trigger both high-priority system status-bar notifications with custom sound channels and full interactive visual popups with scenic mosque and nature backdrops.
> - **Modern Minimal Dark Theme**: Sleek deep obsidian surfaces (`#0C1410`, `#14221C`) paired with subtle emerald accents (`#48D5A3`) and refined gold details (`#D4AF37`) for exceptional visual contrast and battery conservation.

---

## 1. Overview & Core Concept

- **What It Does**: Serves as a full-featured spiritual companion for Muslim daily life: calculating precise astronomical prayer times for any city or GPS location, scheduling exact battery-optimized alarms, offering auto-silent mode during congregation prayers, presenting morning/evening/sleep athkar with contextual nature photography and audio narration, hosting a Quran reader with large scalable typography, displaying categorized Hadith books and Prophet's biography, simulating Ramadan Mesaharaty and Iftar cannon, and presenting home screen widgets.
- **Target Audience**: Muslims seeking an all-in-one, highly customizable Islamic app that respects device battery, runs reliably in the background across restarts, and delivers a modern, visually stunning experience without clutter or intrusive bloat.
- **Key Value**: 100% offline-capable, highly personalized control over every prayer notification, dual interactive home screen widgets, and authentic religious content.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Home & Countdown**: Users see a live second-by-second countdown to the next prayer, current Hijri/Gregorian date, interactive prayer cards with one-tap adhan preview, quick Ramadan/Friday shortcuts, and nature cards for daily Athkar.
2. **Interactive Adhan & Athkar Popups**: When a prayer arrives or is previewed, a full-screen dialog appears featuring grand illuminated mosque photography (`img_mosque_adhan`), current Muezzin audio, Duaa after Adhan, and auto-silent toggle. For Athkar, scenic nature imagery matches the time of day (sunrise valley for morning, quiet lake for evening, starry night for sleep).
3. **Home Screen Widgets**:
   - **Prayer Schedule Widget (4x2 / 4x1)**: Displays upcoming prayer, remaining time, and all daily prayer times with active highlights.
   - **Athkar & Dhikr Widget (2x2 / 4x1)**: Displays a daily rotating dhikr with repetition counter and quick tap to open the electronic Sebha.
4. **Quran Reading**: Fast Surah lookup, scalable Uthmani script font (+/- zoom buttons), ayah markers, and Arabic audio playback.
5. **Ramadan & Eid Suite**: Mesaharaty with drum rhythm and vocal chants, Iftar cannon explosion simulation with Iftar Duaa, Taraweeh silent timer, and Eid Takbeerat player.
6. **Background Scheduling & Auto-Start**: Device boot receiver (`RECEIVE_BOOT_COMPLETED`) registers exact alarms via Android `AlarmManager` without draining battery.

### Visual Identity & Theme
- **Color Tokens**:
  - `Background`: Deep Midnight Obsidian (`#0C1410`)
  - `Surface`: Elevated Charcoal Teal (`#14221C`, `#1A2D25`)
  - `Primary Accent`: Pure Islamic Emerald (`#48D5A3`, `#1B7D63`)
  - `Secondary Accent`: Refined Islamic Gold (`#D4AF37`, `#E8C862`)
  - `Text`: Pure White (`#FFFFFF`) and Soft Sand Green (`#C5D6CE`)
- **Typography**: Clean Arabic typography hierarchy with high legibility, generous line height for Quranic verses, and scalable font controls.
- **Micro-Interactions**: Smooth card ripples, circular progress animations for tasbeeh beads, pulsing drum animation for Mesaharaty, and haptic feedback on counter taps.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Battery-Conscious Alarm Scheduling vs. Continuous Background Services**
  - *Chosen Approach*: Use Android `AlarmManager.setExactAndAllowWhileIdle()` combined with a `BroadcastReceiver` and `RECEIVE_BOOT_COMPLETED` rather than running a constant CPU-draining foreground service.
  - *Why*: Guarantees zero battery drain when idle while waking the device precisely at Adhan or pre-prayer countdown times.
  - *Alternatives Considered*: Continuous Foreground Service with sticky notification (wastes battery, flagged by Android OS).

- **Decision 2: Dual AppWidgets (RemoteViews + Glance-Ready Architecture)**
  - *Chosen Approach*: Implement standard `AppWidgetProvider` with `RemoteViews` for instant compatibility and immediate home screen placement for both Prayer Times and Daily Athkar.
  - *Why*: Supports all Android launchers reliably without external overhead.

- **Decision 3: Complete Offline Audio Synthesis & Arabic Speech**
  - *Chosen Approach*: Procedural acoustic tone synthesizer for Adhan and drum rhythms + Android `TextToSpeech` (Arabic locale).
  - *Why*: Guarantees 100% offline functionality without missing audio files or network buffering delays.

---

## 4. Technical Architecture & Component Hierarchy

```
┌──────────────────────────────────────────────────────────────────┐
│                 Manarat Al-Islam (منارة الإسلام)                 │
└─────────────────────────────────┬────────────────────────────────┘
                                  │
         ┌────────────────────────┼────────────────────────┐
         ▼                        ▼                        ▼
┌──────────────────┐    ┌──────────────────┐    ┌──────────────────┐
│ UI Layer         │    │ Service Layer    │    │ Background &     │
│ (Jetpack Compose)│    │ (Calculation &   │    │ Widgets Layer    │
│                  │    │ Audio Engine)    │    │                  │
├──────────────────┤    ├──────────────────┤    ├──────────────────┤
│ • HomeScreen     │    │ • PrayerCalc     │    │ • BootReceiver   │
│ • AthkarScreen   │    │ • SoundManager   │    │ • AlarmReceiver  │
│ • QuranScreen    │    │ • IslamicRepo    │    │ • PrayerWidget   │
│ • HadithScreen   │    │                  │    │ • AthkarWidget   │
│ • RamadanScreen  │    │                  │    │                  │
│ • SettingsScreen │    │                  │    │                  │
│ • Popups (Adhan/ │    │                  │    │                  │
│   Athkar/Cannon) │    │                  │    │                  │
└──────────────────┘    └──────────────────┘    └──────────────────┘
```

### Next Implementation Steps (Post-Approval)
1. **MainActivity Navigation & Edge-to-Edge Wiring**: Wire all screens into bottom navigation with `Scaffold`, `BackHandler`, and state management.
2. **Dual Home Screen Widgets**:
   - `PrayerTimesWidgetProvider` with layout `widget_prayer_times.xml`.
   - `AthkarWidgetProvider` with layout `widget_athkar.xml`.
3. **Background Boot & Alarm Receivers**:
   - `BootReceiver.kt` listening to `android.intent.action.BOOT_COMPLETED`.
   - `PrayerAlarmReceiver.kt` to trigger audio, notifications, and popup intent.
4. **Manifest Declarations**: Register receivers, widget metadata in `res/xml/`, and permissions.
5. **Compilation Verification**: Run `compile_applet` to ensure zero compilation or resource errors.
