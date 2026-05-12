package xbiconnect.android.driver.data.repository

import xbiconnect.android.driver.data.Resource
import xbiconnect.android.driver.data.api.dto.DriversByVinRequest
import xbiconnect.android.driver.data.api.dto.DriversByVinResponse
import xbiconnect.android.driver.data.network.XbiMainApi
import java.io.IOException

/**
 * Wraps the HOS-by-VIN proxy (`POST /api/hos-api/drivers-by-vin`).
 *
 * The response can carry [Resource.Success] in three meaningful shapes:
 * - success=true with `drivers.main` populated → active driver and HOS available.
 * - success=true with `drivers.main = null` + a `message` → VIN known but no
 *   HOS records in the time window.
 * - success=false → VIN not in vehicles.
 *
 * Callers branch on those, not on [Resource]. [Resource.Error] is reserved for
 * actual transport failures.
 */
class DriversRepository(
    private val api: XbiMainApi,
) {
    suspend fun driversByVin(vin: String): Resource<DriversByVinResponse> = try {
        val response = api.driversByVin(DriversByVinRequest(vin = vin))
        Resource.Success(response)
    } catch (e: IOException) {
        Resource.Error(
            message = "Sin conexión con el servidor HOS.",
            cause = e,
        )
    } catch (e: Exception) {
        Resource.Error(
            message = e.message ?: "Error al consultar al chofer.",
            cause = e,
        )
    }
}
