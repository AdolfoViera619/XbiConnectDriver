package xbiconnect.android.driver.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import xbiconnect.android.driver.data.api.dto.VehicleDto

private val Context.dataStore by preferencesDataStore(name = "driver_prefs")

enum class ServerMode { LOCAL, PROD }

/**
 * Snapshot of the data persisted at pairing time. Everything in this struct
 * lives across launches and gets cleared together when the tablet is unbound.
 */
data class PairedVehicle(
    val vin: String,
    val vehicleId: Long?,
    val label: String?,
    val make: String?,
    val model: String?,
    val year: String?,
    val driveLine: String?,
    val engine: String?,
    val customerId: Long?,
    val customerName: String?,
    val customerLogoUrl: String?,
    val instanceUrl: String?,
    val apiToken: String?,
    val database: String?,
    /**
     * Chatwoot Channel::Api `identifier` token for the driver inbox of this
     * customer. Today the gateway's `validate-vin` does not include this in
     * its response — when in [ServerMode.LOCAL] development the app falls
     * back to a hardcoded dev identifier (see [DriverConfig]). When the
     * gateway is extended to return it, persistence handles it here.
     */
    val inboxIdentifier: String?,
)

/**
 * Persists the tablet ↔ truck binding plus the customer-instance metadata
 * returned by `validate-vin`. After pairing, subsequent customer-scoped
 * traffic uses [PairedVehicle.instanceUrl] as base URL and
 * [PairedVehicle.apiToken] as the bearer credential.
 */
class DriverPreferences(private val context: Context) {

    private val vinKey = stringPreferencesKey("vin")
    private val serverKey = stringPreferencesKey("server_mode")
    private val customUrlKey = stringPreferencesKey("server_custom_url")

    // Pairing snapshot — written together when the user confirms TruckFound.
    private val vehicleIdKey = longPreferencesKey("vehicle_id")
    private val labelKey = stringPreferencesKey("vehicle_label")
    private val makeKey = stringPreferencesKey("vehicle_make")
    private val modelKey = stringPreferencesKey("vehicle_model")
    private val yearKey = stringPreferencesKey("vehicle_year")
    private val driveLineKey = stringPreferencesKey("vehicle_drive_line")
    private val engineKey = stringPreferencesKey("vehicle_engine")
    private val customerIdKey = longPreferencesKey("customer_id")
    private val customerNameKey = stringPreferencesKey("customer_name")
    private val customerLogoKey = stringPreferencesKey("customer_logo")
    private val instanceUrlKey = stringPreferencesKey("instance_url")
    private val apiTokenKey = stringPreferencesKey("api_token")
    private val databaseKey = stringPreferencesKey("database")
    private val inboxIdentifierKey = stringPreferencesKey("inbox_identifier")

    val vin: Flow<String?> = context.dataStore.data.map { it[vinKey] }

    val serverMode: Flow<ServerMode> = context.dataStore.data.map {
        when (it[serverKey]) {
            "LOCAL" -> ServerMode.LOCAL
            else -> ServerMode.PROD
        }
    }

    val customUrl: Flow<String?> = context.dataStore.data.map { it[customUrlKey] }

    val pairedVehicle: Flow<PairedVehicle?> = context.dataStore.data.map { prefs ->
        val vin = prefs[vinKey] ?: return@map null
        PairedVehicle(
            vin = vin,
            vehicleId = prefs[vehicleIdKey],
            label = prefs[labelKey],
            make = prefs[makeKey],
            model = prefs[modelKey],
            year = prefs[yearKey],
            driveLine = prefs[driveLineKey],
            engine = prefs[engineKey],
            customerId = prefs[customerIdKey],
            customerName = prefs[customerNameKey],
            customerLogoUrl = prefs[customerLogoKey],
            instanceUrl = prefs[instanceUrlKey],
            apiToken = prefs[apiTokenKey],
            database = prefs[databaseKey],
            inboxIdentifier = prefs[inboxIdentifierKey],
        )
    }

    /**
     * Commits the full pairing snapshot in a single write so the [pairedVehicle]
     * flow flips from null to populated atomically.
     */
    suspend fun savePairing(
        vin: String,
        vehicle: VehicleDto?,
        customer: xbiconnect.android.driver.data.api.dto.CustomerDto?,
        instanceUrl: String?,
        database: String?,
        inboxIdentifier: String? = null,
    ) {
        context.dataStore.edit { prefs ->
            prefs[vinKey] = vin
            vehicle?.id?.let { prefs[vehicleIdKey] = it }
            vehicle?.label?.let { prefs[labelKey] = it }
            vehicle?.make?.let { prefs[makeKey] = it }
            vehicle?.model?.let { prefs[modelKey] = it }
            vehicle?.year?.let { prefs[yearKey] = it }
            vehicle?.driveLine?.let { prefs[driveLineKey] = it }
            vehicle?.engine?.let { prefs[engineKey] = it }
            customer?.id?.let { prefs[customerIdKey] = it }
            customer?.contact?.let { prefs[customerNameKey] = it }
            customer?.logo?.let { prefs[customerLogoKey] = it }
            instanceUrl?.let { prefs[instanceUrlKey] = it }
            customer?.apiToken?.let { prefs[apiTokenKey] = it }
            database?.let { prefs[databaseKey] = it }
            inboxIdentifier?.let { prefs[inboxIdentifierKey] = it }
        }
    }

    suspend fun clearPairing() {
        context.dataStore.edit { prefs ->
            prefs.remove(vinKey)
            prefs.remove(vehicleIdKey)
            prefs.remove(labelKey)
            prefs.remove(makeKey)
            prefs.remove(modelKey)
            prefs.remove(yearKey)
            prefs.remove(driveLineKey)
            prefs.remove(engineKey)
            prefs.remove(customerIdKey)
            prefs.remove(customerNameKey)
            prefs.remove(customerLogoKey)
            prefs.remove(instanceUrlKey)
            prefs.remove(apiTokenKey)
            prefs.remove(databaseKey)
            prefs.remove(inboxIdentifierKey)
        }
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
