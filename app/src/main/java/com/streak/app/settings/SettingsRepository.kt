package com.streak.app.settings

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.streak.app.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "streak_settings")

/** Persists the user settings (currently the theme mode) with DataStore. */
class SettingsRepository(private val context: Context) {

    val themeMode: Flow<ThemeMode> = context.settingsDataStore.data
        .map { preferences -> ThemeMode.fromName(preferences[KEY_THEME_MODE]) }
        .catch { error ->
            Log.w(TAG, "Could not read settings: ${error.message}")
            emit(ThemeMode.SYSTEM)
        }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = mode.name
        }
        Log.i(TAG, "Theme mode stored: ${mode.name}")
    }

    companion object {
        const val TAG = "StreakSettings"
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
    }
}
