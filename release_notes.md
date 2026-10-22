## Streak v1.0.0

A native Android habit tracker: Kotlin, Jetpack Compose (Material 3), Room, DataStore and WorkManager.

**Install `app-release.apk`** — it is signed with the project key so you can upgrade in place.

### Features

- **Home** — habit list with a "done today" checkbox, current streak and best streak
- **Add / edit** — name, emoji (16 presets or custom), optional daily reminder time, delete
- **Detail** — last 30 days as a done/missed grid, current streak, best streak, days done; tap a day to correct it
- **Reminders** — a daily notification at the time you picked (WorkManager, `POST_NOTIFICATIONS` requested on Android 13+)
- **Settings** — System / Light / Dark theme stored in DataStore
- **Persistence** — habits, history and settings survive closing the app

### Requirements

Android 8.0 (API 28) or newer.

### Build it yourself

Every push to `main` runs GitHub Actions: unit tests → `assembleDebug` + `assembleRelease` → APKs uploaded here
and as workflow artifacts. See the README for local build instructions.

### Upgrade note

`app-debug.apk` is signed with the throw-away Android debug key and cannot replace the release build
(uninstall it first if you switch between the two).
