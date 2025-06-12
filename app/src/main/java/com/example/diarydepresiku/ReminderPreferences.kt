package com.example.diarydepresiku

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val Context.dataStore by preferencesDataStore(name = "reminder_preferences")

class ReminderPreferences(private val context: Context) {

    private val formatter = DateTimeFormatter.ISO_LOCAL_TIME

    val reminderEnabled: Flow<Boolean> =
        context.dataStore.data.map { it[KEY_ENABLED] ?: false }

    val reminderTime: Flow<LocalTime> =
        context.dataStore.data.map {
            it[KEY_TIME]?.let { t -> LocalTime.parse(t, formatter) } ?: LocalTime.of(8, 0)
        }

    val darkMode: Flow<Boolean> =
        context.dataStore.data.map { it[KEY_DARK_MODE] ?: false }

    val fontScale: Flow<Float> =
        context.dataStore.data.map { it[KEY_FONT_SCALE]?.toFloat() ?: 1f }

    suspend fun setReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_ENABLED] = enabled }
    }

    suspend fun setReminderTime(time: LocalTime) {
        context.dataStore.edit { it[KEY_TIME] = time.format(formatter) }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DARK_MODE] = enabled }
    }

    suspend fun setFontScale(scale: Float) {
        context.dataStore.edit { it[KEY_FONT_SCALE] = scale.toString() }
    }

    companion object {
        private val KEY_ENABLED = booleanPreferencesKey("reminder_enabled")
        private val KEY_TIME = stringPreferencesKey("reminder_time")
        private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        private val KEY_FONT_SCALE = stringPreferencesKey("font_scale")
    }
}
