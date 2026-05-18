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
 * Body for `POST /public/api/v1/inboxes/{identifier}/contacts`.
 *
 * Chatwoot's public contacts controller reads two distinct fields:
 *  - `source_id` (top-level param) → goes into `ContactInbox.source_id`,
 *    which is what the URL paths look up. **If omitted, the server falls
 *    back to a random UUID** and the contact_inbox ends up with a source_id
 *    different from what we sent — subsequent calls keyed by VIN return 404.
 *  - `identifier` → goes into `Contact.identifier` at the account level
 *    (used for cross-inbox dedup by external id).
 *
 * For the Driver app the truck is uniquely identified by its VIN, so both
 * fields receive the same value.
 */
data class CreateContactRequest(
    @SerializedName("source_id") val sourceId: String,
    @SerializedName("identifier") val identifier: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone_number") val phoneNumber: String? = null,
    @SerializedName("custom_attributes") val customAttributes: Map<String, Any?>? = null,
)
