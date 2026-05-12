package xbiconnect.android.driver.data.repository

import xbiconnect.android.driver.data.Resource
import xbiconnect.android.driver.data.api.dto.ValidateVinRequest
import xbiconnect.android.driver.data.api.dto.ValidateVinResponse
import xbiconnect.android.driver.data.network.XbiMainApi
import java.io.IOException

/**
 * Wraps the cross-instance pairing call (`POST /api/auth/validate-vin`).
 *
 * Returns:
 * - [Resource.Success] with the full response when the gateway answers,
 *   regardless of whether the VIN existed — UI inspects `response.success`
 *   to decide if it's a valid match or "VIN not found".
 * - [Resource.Error] only when the network call itself blew up (no
 *   connectivity, malformed JSON, timeout, etc).
 */
class VehiclePairingRepository(
    private val api: XbiMainApi,
) {
    suspend fun validateVin(vin: String): Resource<ValidateVinResponse> = try {
        val response = api.validateVin(ValidateVinRequest(vin = vin))
        Resource.Success(response)
    } catch (e: IOException) {
        Resource.Error(
            message = "No se pudo conectar con el servidor. Verifica tu conexión.",
            cause = e,
        )
    } catch (e: Exception) {
        Resource.Error(
            message = e.message ?: "Error inesperado al validar el VIN.",
            cause = e,
        )
    }
}
