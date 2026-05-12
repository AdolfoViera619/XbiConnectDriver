package xbiconnect.android.driver.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Chatwoot User (agent/admin). For the Driver app, this represents the sender
 * of a message (dispatcher / supervisor) — we don't store driver users locally
 * since drivers don't log in.
 */
data class UserDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("available_name") val availableName: String?,
    @SerializedName("account_id") val accountId: Int?,
    @SerializedName("role") val role: String?,
)

/** Slim view of a sender as embedded in messages and conversations. */
data class SenderDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("type") val type: String?,
    @SerializedName("custom_attributes") val customAttributes: Map<String, Any?>? = null,
)
