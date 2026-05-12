package xbiconnect.android.driver.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Chatwoot Inbox. The Driver app filters by [driverChannel] = true to only
 * surface conversations relevant to the truck.
 */
data class InboxDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String?,
    @SerializedName("channel_type") val channelType: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("greeting_enabled") val greetingEnabled: Boolean? = null,
    @SerializedName("greeting_message") val greetingMessage: String? = null,
    @SerializedName("driver_channel") val driverChannel: Boolean = false,
    @SerializedName("lock_to_single_conversation") val lockToSingleConversation: Boolean = false,
)

data class InboxesResponse(
    @SerializedName("payload") val payload: List<InboxDto>,
)
