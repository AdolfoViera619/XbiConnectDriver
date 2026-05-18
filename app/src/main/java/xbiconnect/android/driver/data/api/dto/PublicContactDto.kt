package xbiconnect.android.driver.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Response from `POST /public/api/v1/inboxes/{identifier}/contacts` and
 * `GET .../contacts/{source_id}`.
 *
 * For the Driver app the contact represents the TRUCK. We persist:
 * - `id` (Chatwoot's numeric contact id — needed for some downstream calls)
 * - `sourceId` (the VIN we sent; how we'll look it up next time)
 * - `pubsubToken` (needed when we wire ActionCable for real-time)
 */
data class PublicContactDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("source_id") val sourceId: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("identifier_hash") val identifierHash: String?,
    @SerializedName("pubsub_token") val pubsubToken: String?,
    @SerializedName("custom_attributes") val customAttributes: Map<String, Any?>? = null,
    @SerializedName("additional_attributes") val additionalAttributes: Map<String, Any?>? = null,
)

/**
 * Body for `POST /public/api/v1/inboxes/{identifier}/contacts`. Chatwoot's
 * public-API contracts use snake_case. `identifier` here is the contact's
 * `source_id` — for the Driver app we send the VIN.
 */
data class CreateContactRequest(
    @SerializedName("identifier") val sourceId: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone_number") val phoneNumber: String? = null,
    @SerializedName("custom_attributes") val customAttributes: Map<String, Any?>? = null,
)
