package xbiconnect.android.driver.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Chatwoot Conversation. Slimmed to what the Driver UI needs:
 * the unread count, status, last activity, last message preview, and the
 * inbox/channel info so we know which inbox the conversation belongs to.
 */
data class ConversationDto(
    @SerializedName("id") val id: Int,
    @SerializedName("uuid") val uuid: String? = null,
    @SerializedName("account_id") val accountId: Int? = null,
    @SerializedName("inbox_id") val inboxId: Int,
    @SerializedName("driver_channel") val driverChannel: Boolean? = null,
    @SerializedName("status") val status: ConversationStatus = ConversationStatus.OPEN,
    @SerializedName("muted") val muted: Boolean = false,
    @SerializedName("unread_count") val unreadCount: Int = 0,
    @SerializedName("labels") val labels: List<String> = emptyList(),
    @SerializedName("created_at") val createdAt: UnixTimestamp = 0,
    @SerializedName("last_activity_at") val lastActivityAt: UnixTimestamp = 0,
    @SerializedName("contact_last_seen_at") val contactLastSeenAt: UnixTimestamp? = null,
    @SerializedName("agent_last_seen_at") val agentLastSeenAt: UnixTimestamp? = null,
    @SerializedName("can_reply") val canReply: Boolean = true,
    @SerializedName("messages") val messages: List<MessageDto> = emptyList(),
    @SerializedName("meta") val meta: ConversationMetaDto? = null,
)

data class ConversationMetaDto(
    @SerializedName("sender") val sender: SenderDto? = null,
    @SerializedName("assignee") val assignee: UserDto? = null,
    @SerializedName("channel") val channel: Channel? = null,
)

data class ConversationsResponse(
    @SerializedName("data") val data: ConversationsData,
)

data class ConversationsData(
    @SerializedName("meta") val meta: ConversationsListMeta? = null,
    @SerializedName("payload") val payload: List<ConversationDto>,
)

data class ConversationsListMeta(
    @SerializedName("mine_count") val mineCount: Int? = null,
    @SerializedName("unassigned_count") val unassignedCount: Int? = null,
    @SerializedName("all_count") val allCount: Int? = null,
)
