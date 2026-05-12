package xbiconnect.android.driver.data.network

import retrofit2.http.Body
import retrofit2.http.POST
import xbiconnect.android.driver.data.api.dto.DriversByVinRequest
import xbiconnect.android.driver.data.api.dto.DriversByVinResponse
import xbiconnect.android.driver.data.api.dto.ValidateVinRequest
import xbiconnect.android.driver.data.api.dto.ValidateVinResponse

/**
 * Cross-instance endpoints exposed by the main XBI gateway.
 * These do not require auth — the gateway routes by VIN internally and returns
 * the per-customer instance URL plus the api_token to use against it.
 */
interface XbiMainApi {

    @POST("api/auth/validate-vin")
    suspend fun validateVin(@Body body: ValidateVinRequest): ValidateVinResponse

    @POST("api/hos-api/drivers-by-vin")
    suspend fun driversByVin(@Body body: DriversByVinRequest): DriversByVinResponse
}
