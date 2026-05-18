package xbiconnect.android.driver.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Shape returned by the public Channel::Api conversation jbuilder
 * (`app/views/public/api/v1/models/_conversation.json.jbuilder` in the
 * Chatwoot fork — verified by Mediador msg 003). Strictly fewer fields
 * than the authenticated dashboard endpoint:
 *
 * - id, uuid, inbox_id
 * - status (OPEN / RESOLVED / …)
 * - contact_last_seen_at, agent_last_seen_at
 * - messages (embedded list)
 * - contact (slim summary)
 *
 * Notably: no `driver_channel`, no `meta`, no labels/priority/etc. That's
 * fine — the tablet only talks to its own inbox so it doesn't need to
 * branch on the flag.
 */
data class PublicConversationDto(
    @SerializedName("id") val id: Int,
    @SerializedName("uuid") val uuid: String? = null,
    @SerializedName("inbox_id") val inboxId: Int? = null,
    @SerializedName("status") val status: ConversationStatus = ConversationStatus.OPEN,
    @SerializedName("contact_last_seen_at") val contactLastSeenAt: UnixTimestamp? = null,
    @SerializedName("agent_last_seen_at") val agentLastSeenAt: UnixTimestamp? = null,
    @SerializedName("messages") val messages: List<MessageDto> = emptyList(),
    @SerializedName("contact") val contact: PublicContactDto? = null,
)

/**
 * Some Chatwoot endpoints wrap collections in `payload`; others return a
 * bare array. The public conversation list returns a bare array, so this
 * type alias is just for self-documenting call sites.
 */
typealias PublicConversationList = List<PublicConversationDto>
