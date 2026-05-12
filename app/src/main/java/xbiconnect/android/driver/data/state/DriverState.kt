package xbiconnect.android.driver.data.state

import xbiconnect.android.driver.data.api.dto.HosDriverDto

/**
 * UI state for the currently assigned driver of the paired vehicle.
 *
 * The [Active] case carries the [HosDriverDto] from `drivers-by-vin`. The
 * `current_duty_status_label` field on that DTO is already in display form
 * (e.g. "Driving" / "Off Duty" / "Sleeper Berth") — UI can render it directly
 * without mapping the raw code.
 */
sealed interface DriverState {
    data object Loading : DriverState
    data class Active(val main: HosDriverDto, val coDriver: HosDriverDto?) : DriverState
    data class NoData(val message: String?) : DriverState
    data class Error(val message: String) : DriverState
}
