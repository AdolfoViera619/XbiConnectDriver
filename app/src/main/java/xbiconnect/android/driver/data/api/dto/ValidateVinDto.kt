package xbiconnect.android.driver.data.api.dto

import com.google.gson.annotations.SerializedName

data class ValidateVinRequest(
    @SerializedName("vin") val vin: String,
)

/**
 * Response from POST /api/auth/validate-vin
 *
 * Success shape: `success: true` + `vehicle` + `customer` + `database` + `url`.
 * Error shape: `success: false` + `message` only.
 *
 * `url` carries the per-customer instance URL — that's what subsequent
 * customer-scoped calls (messages, conversations, etc.) must hit.
 * `customer.api_token` is the auth token to use against that instance.
 */
data class ValidateVinResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("database") val database: String?,
    @SerializedName("database_name") val databaseName: String?,
    @SerializedName("url") val instanceUrl: String?,
    @SerializedName("source_table") val sourceTable: String?,
    @SerializedName("vehicle") val vehicle: VehicleDto?,
    @SerializedName("customer") val customer: CustomerDto?,
)

data class VehicleDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("label") val label: String?,
    @SerializedName("vin") val vin: String?,
    @SerializedName("status") val status: Int?,
    @SerializedName("odometer") val odometer: String?,
    @SerializedName("model") val model: String?,
    @SerializedName("drive_line") val driveLine: String?,
    @SerializedName("engine") val engine: String?,
    @SerializedName("year") val year: String?,
    @SerializedName("make") val make: String?,
    @SerializedName("customer_id") val customerId: Long?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
)

data class CustomerDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("api_token") val apiToken: String?,
    @SerializedName("contact") val contact: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("dotNumber") val dotNumber: String?,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("phone_number_secondary") val phoneNumberSecondary: String?,
    @SerializedName("active") val active: Int?,
    @SerializedName("address") val address: String?,
    @SerializedName("number") val number: String?,
    @SerializedName("colony") val colony: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("state") val state: String?,
    @SerializedName("zip") val zip: Int?,
    @SerializedName("complete_address") val completeAddress: String?,
    @SerializedName("geoCoord") val geoCoord: String?,
    @SerializedName("logo") val logo: String?,
    @SerializedName("gps_type") val gpsType: Int?,
    @SerializedName("e_book") val eBook: String?,
)
