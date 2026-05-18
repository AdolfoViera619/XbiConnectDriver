package xbiconnect.android.driver.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Chatwoot Message. Only the fields the Driver UI needs are kept; attachments
 * and reactions can be added later if the chat feature requires them.
 */
data class MessageDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("content") val content: String? = null,
    @SerializedName("content_type") val contentType: ContentType? = ContentType.TEXT,
    @SerializedName("message_type") val messageType: MessageType = MessageType.INCOMING,
    @SerializedName("created_at") val createdAt: UnixTimestamp = 0,
    @SerializedName("private") val private: Boolean = false,
    @SerializedName("status") val status: MessageStatus? = MessageStatus.SENT,
    @SerializedName("source_id") val sourceId: String? = null,
    @SerializedName("sender") val sender: SenderDto? = null,
    @SerializedName("sender_id") val senderId: Int? = null,
    @SerializedName("sender_type") val senderType: String? = null,
    @SerializedName("conversation_id") val conversationId: Int = 0,
    @SerializedName("inbox_id") val inboxId: Int? = null,
    @SerializedName("echo_id") val echoId: String? = null,
    @SerializedName("attachments") val attachments: List<AttachmentDto>? = null,
)

data class AttachmentDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("message_id") val messageId: Int = 0,
    @SerializedName("file_type") val fileType: String = "file",
    @SerializedName("data_url") val dataUrl: String? = null,
    @SerializedName("thumb_url") val thumbUrl: String? = null,
    @SerializedName("extension") val extension: String? = null,
)

data class MessagesResponse(
    @SerializedName("meta") val meta: Map<String, Any?>? = null,
    @SerializedName("payload") val payload: List<MessageDto>,
)

/**
 * Body for the public Channel::Api send-message endpoint
 * (`POST /public/api/v1/inboxes/{identifier}/contacts/{source_id}/conversations/{id}/messages`).
 *
 * The public endpoint auto-sets `message_type` to incoming and ignores
 * `private`, so the body is just the content plus an optional client-side
 * echo id to deduplicate when the message comes back via WebSocket.
 */
data class SendMessageRequest(
    @SerializedName("content") val content: String,
    @SerializedName("echo_id") val echoId: String? = null,
)
