# Streak

A native Android habit tracker built with **Kotlin**, **Jetpack Compose (Material 3)** and **Navigation Compose**.
Habits, their daily completions and the settings live on the device — Room for the data, DataStore for the
preferences and WorkManager for the daily reminders.

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF.svg" alt="Kotlin">
  <img src="https://img.shields.io/badge/Compose-Material%203-3DDC84.svg" alt="Compose">
  <img src="https://img.shields.io/badge/Room-2.6.1-2E8B57.svg" alt="Room">
  <img src="https://img.shields.io/badge/minSdk-28-3DDC84.svg" alt="minSdk 28">
  <img src="https://img.shields.io/badge/targetSdk-34-3DDC84.svg" alt="targetSdk 34">
</p>

---

## Features

| # | Feature | Where |
|---|---------|-------|
| 1 | **Home screen** – list of habits, a checkbox for *today* and the current streak (plus the best streak) | `ui/home` |
| 2 | **Add / edit habit** – name, emoji picker (16 presets + custom), optional daily reminder time, delete | `ui/editor` |
| 3 | **Detail screen** – last 30 days as a done/missed grid, current streak, best streak, total days done; tap a day to correct it | `ui/detail` |
| 4 | **Daily reminder notification** – scheduled with WorkManager, `POST_NOTIFICATIONS` requested on Android 13+ | `reminders` |
| 5 | **Settings** – theme (System / Light / Dark) stored in DataStore, notification permission helper, about section | `ui/settings` |
| 6 | **Persistence** – everything survives closing the app (Room database + DataStore preferences) | `data`, `settings` |

### How the streak is calculated

`data/StreakCalculator.kt` contains the pure streak arithmetic (no Android dependencies, fully unit tested):

* **Current streak** – consecutive completed days ending today. A day that is still pending does *not* break the
  streak, it is simply counted from yesterday backwards.
* **Best streak** – the longest run of consecutive completed days ever recorded.
* Days are identified by their *epoch day* number in the device's local time zone, so a completion always maps to
  exactly one calendar day.

## Tech stack

| Concern | Library |
|---------|---------|
| Language | Kotlin 2.0.21 (JVM target 17) |
| UI | Jetpack Compose, Material 3, Navigation Compose |
| Architecture | MVVM with `StateFlow` + `ViewModel`, manual DI (`AppContainer`) |
| Storage | Room 2.6.1 (KSP), DataStore Preferences 1.1.1 |
| Background work | WorkManager 2.9.1 |
| Build | AGP 8.5.2, Gradle 8.7, GitHub Actions |

* `namespace` / `applicationId`: **`com.streak.app`**
* `minSdk` **28**, `targetSdk` **34**, `compileSdk` **34**

## Project layout

```
app/src/main/java/com/streak/app/
├── AppContainer.kt              # manual dependency container
├── MainActivity.kt              # single activity, hosts the Compose nav graph
├── StreakApplication.kt         # application class (notification channel)
├── data/
│   ├── Habit.kt                 # Room entity
│   ├── HabitCompletion.kt       # Room entity (habitId + epochDay)
│   ├── HabitDao.kt              # habit queries
│   ├── CompletionDao.kt         # completion queries
│   ├── StreakDatabase.kt        # Room database
│   ├── HabitRepository.kt       # single source of truth + logging
│   └── StreakCalculator.kt      # pure streak logic
├── reminders/
│   ├── NotificationHelper.kt    # channel + posting notifications
│   ├── ReminderScheduler.kt     # enqueues/cancels WorkManager jobs
│   └── ReminderWorker.kt        # posts the notification, re-schedules tomorrow
├── settings/
│   ├── SettingsRepository.kt    # DataStore (theme mode)
│   └── SettingsViewModel.kt
├── ui/
│   ├── home/                    # home screen + view model
│   ├── editor/                  # add / edit screen + view model
│   ├── detail/                  # detail screen with 30 day grid
│   ├── settings/                # settings screen
│   ├── navigation/              # routes + NavHost
│   └── theme/                   # Material 3 colour scheme + ThemeMode
├── util/Dates.kt                # epoch day helpers
└── ui/rememberAppContainer.kt
```

## Build

### Option A — GitHub Actions (no local Android SDK needed)

Every push to `main` runs [`.github/workflows/android.yml`](.github/workflows/android.yml), which:

1. sets up JDK 17 and Gradle 8.7,
2. runs the unit tests (`testDebugUnitTest`),
3. builds `assembleDebug` **and** `assembleRelease`,
4. uploads the debug APK as a workflow artifact,
5. publishes both APKs to a rolling GitHub Release (`build-<run number>`).

Grab the APK from the **Releases** page or the workflow run's **Artifacts** section.

### Option B — locally

```bash
git clone https://github.com/farsanahmed/streak-app.git
cd streak-app

# point the build at your SDK (or set ANDROID_HOME)
echo "sdk.dir=/path/to/Android/sdk" > local.properties

./gradlew testDebugUnitTest     # unit tests
./gradlew assembleDebug         # debug APK   -> app/build/outputs/apk/debug/app-debug.apk
./gradlew assembleRelease       # release APK -> app/build/outputs/apk/release/app-release.apk
```

The release variant is signed with `keystore/release.p12` (password `streak`, alias `streak`) when that file is
present, so every build produces an APK with the same signature and the app can be upgraded in place.
**The key is committed on purpose so that CI can sign the release build – do not use this key for a real product.**

## Install

```bash
# from the release page
gh release download v1.0.0 --repo farsanahmed/streak-app --pattern "app-release.apk"

# or directly on the phone (USB debugging enabled)
adb install -r app-release.apk
```

Install `app-release.apk` (signed with the project key). The `app-debug.apk` artifact is signed with the
throw-away Android debug key, so the two cannot replace each other – uninstall the debug build first if you
switch between them.

## Reminders in detail

* The reminder time is stored with the habit (`reminderHour` / `reminderMinute`).
* Saving a habit enqueues a **one-time** WorkManager request for the next occurrence
  (`habit-reminder-<id>-<epochDay>`); no exact-alarm permission is needed.
* `ReminderWorker` posts the notification and schedules the request for the following day, so the reminder keeps
  repeating without any extra setup.
* Deleting a habit cancels all of its pending requests.
* On Android 13+ the `POST_NOTIFICATIONS` permission is requested when a reminder is switched on and can also be
  requested from **Settings → Notifications**.

## Logging

Logcat tags used by the app:

| Tag | Content |
|-----|---------|
| `StreakDb` | database creation |
| `StreakRepo` | habit insert/update/delete, completion toggles |
| `StreakReminder` | scheduling, cancelling, posting notifications |
| `StreakSettings` | theme mode changes |
| `StreakEditor` | notification permission results |

```bash
adb logcat -s StreakDb StreakRepo StreakReminder StreakSettings
```

## Milestones

| Milestone | Content | Status |
|-----------|---------|--------|
| M1 | Project skeleton, Material 3 theme, navigation, home screen with sample data | ✅ green |
| M2 | Room database, repository, add/edit habit screen | ✅ green |
| M3 | Streak logic + unit tests, detail screen with the 30 day grid | ✅ green |
| M4 | WorkManager reminders + notifications, DataStore theme settings, settings screen | ✅ green |

## License

MIT
