package xbiconnect.android.driver.data.state

import xbiconnect.android.driver.data.api.dto.ValidateVinResponse

/** UI state of the onboarding pair flow. */
sealed interface PairingState {
    /** No request in flight, no error to show. */
    data object Idle : PairingState

    /** `validate-vin` is being awaited. */
    data object Loading : PairingState

    /** Gateway answered and returned a real vehicle — ready for TruckFound. */
    data class Found(val response: ValidateVinResponse) : PairingState

    /**
     * Surface an error inline on the onboarding screen. Covers both transport
     * failures and the `success: false` case (VIN not in fleet).
     */
    data class Error(val message: String) : PairingState
}
