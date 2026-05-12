package xbiconnect.android.driver.data.api.dto

import com.google.gson.annotations.SerializedName

data class DriversByVinRequest(
    @SerializedName("vin") val vin: String,
)

/**
 * Response from POST /api/hos-api/drivers-by-vin
 *
 * Variants observed:
 * - VIN with active HOS data: `drivers.main` populated, `latest_event` populated.
 * - VIN known to backend but with no HOS in window: `drivers.main = null`,
 *   `latest_event = null`, and `message` describes why.
 * - VIN not in vehicles: `success = false`, `message = "VIN no encontrado..."`.
 *
 * Note that `success = true` does NOT imply driver data exists — callers must
 * check `drivers.main != null` before reading.
 */
data class DriversByVinResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("vin") val vin: String?,
    @SerializedName("customer_id") val customerId: Long?,
    @SerializedName("tractor_number") val tractorNumber: String?,
    @SerializedName("window_hours") val windowHours: Int?,
    @SerializedName("latest_event") val latestEvent: LatestEventDto?,
    @SerializedName("drivers") val drivers: DriversDto?,
)

data class DriversDto(
    @SerializedName("main") val main: HosDriverDto?,
    @SerializedName("co_driver") val coDriver: HosDriverDto?,
)

data class HosDriverDto(
    @SerializedName("HOSDriverId") val hosDriverId: Long?,
    @SerializedName("name") val name: String?,
    @SerializedName("username") val username: String?,
    /**
     * ELD duty status code. Observed values: "D" (Driving), "OFF" (Off Duty),
     * "SB" (Sleeper Berth). Likely also "ON" (On Duty). Treat as free string —
     * use [statusLabel] for display.
     */
    @SerializedName("current_duty_status") val statusCode: String?,
    @SerializedName("current_duty_status_label") val statusLabel: String?,
    @SerializedName("last_location") val lastLocation: String?,
    @SerializedName("last_update_timestamp") val lastUpdateTimestamp: Long?,
    /** Format observed: "HH:MM/HH:MM" — used/limit. */
    @SerializedName("driving_time_string") val drivingTime: String?,
    @SerializedName("on_duty_time_string") val onDutyTime: String?,
)

data class LatestEventDto(
    @SerializedName("timestamp") val timestamp: Long?,
    @SerializedName("new_driver_status") val newDriverStatus: String?,
    @SerializedName("event_type") val eventType: Int?,
    @SerializedName("event_code") val eventCode: Int?,
    @SerializedName("location") val location: String?,
)
