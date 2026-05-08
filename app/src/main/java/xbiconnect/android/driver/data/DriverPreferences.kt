package xbiconnect.android.driver.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "driver_prefs")

enum class ServerMode { LOCAL, PROD }

/**
 * Truck binding + technical config persisted across launches.
 *
 * VIN is the tablet's identity — set once during onboarding, cleared
 * when the user explicitly unbinds the tablet via Settings.
 */
class DriverPreferences(private val context: Context) {

    private val vinKey = stringPreferencesKey("vin")
    private val serverKey = stringPreferencesKey("server_mode")
    private val customUrlKey = stringPreferencesKey("server_custom_url")

    val vin: Flow<String?> = context.dataStore.data.map { it[vinKey] }

    val serverMode: Flow<ServerMode> = context.dataStore.data.map {
        when (it[serverKey]) {
            "LOCAL" -> ServerMode.LOCAL
            else -> ServerMode.PROD
        }
    }

    val customUrl: Flow<String?> = context.dataStore.data.map { it[customUrlKey] }

    suspend fun setVin(value: String) {
        context.dataStore.edit { it[vinKey] = value }
    }

    suspend fun clearVin() {
        context.dataStore.edit { it.remove(vinKey) }
    }

    suspend fun setServerMode(mode: ServerMode) {
        context.dataStore.edit { it[serverKey] = mode.name }
    }

    suspend fun setCustomUrl(url: String?) {
        context.dataStore.edit { prefs ->
            if (url.isNullOrBlank()) prefs.remove(customUrlKey)
            else prefs[customUrlKey] = url
        }
    }
}

val LocalDriverPreferences =
    androidx.compose.runtime.staticCompositionLocalOf<DriverPreferences> {
        error("DriverPreferences not provided")
    }
