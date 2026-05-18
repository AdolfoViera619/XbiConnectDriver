package xbiconnect.android.driver.data.network

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import xbiconnect.android.driver.data.api.dto.CreateContactRequest
import xbiconnect.android.driver.data.api.dto.MessageDto
import xbiconnect.android.driver.data.api.dto.PublicContactDto
import xbiconnect.android.driver.data.api.dto.PublicConversationDto
import xbiconnect.android.driver.data.api.dto.SendMessageRequest

/**
 * Chatwoot Channel::Api public endpoints — used by the tablet to act as
 * the truck (a Contact in Chatwoot) on its driver inbox.
 *
 * No auth headers required: the inbox `identifier` in the URL plus the
 * Contact's `source_id` (the VIN) are the credentials. HMAC may be added
 * later (Mediador msg 003 says it's off in dev; if enabled in prod we
 * inject `identifier_hash` in the request body / header).
 *
 * Base URL is set by [ApiClient] depending on [xbiconnect.android.driver.data.ServerMode].
 *
 * Paths use the public segment `public/api/v1/inboxes/{inbox_id}` where
 * `inbox_id` is actually the channel `identifier` token (verified against
 * `app/controllers/public/api/v1/inboxes_controller.rb` in the Rails repo).
 */
interface XbiInstanceApi {

    @POST("public/api/v1/inboxes/{inbox_id}/contacts")
    suspend fun createContact(
        @Path("inbox_id") inboxIdentifier: String,
        @Body body: CreateContactRequest,
    ): PublicContactDto

    @GET("public/api/v1/inboxes/{inbox_id}/contacts/{source_id}")
    suspend fun getContact(
        @Path("inbox_id") inboxIdentifier: String,
        @Path("source_id") sourceId: String,
    ): PublicContactDto

    @POST("public/api/v1/inboxes/{inbox_id}/contacts/{source_id}/conversations")
    suspend fun createConversation(
        @Path("inbox_id") inboxIdentifier: String,
        @Path("source_id") sourceId: String,
    ): PublicConversationDto

    @GET("public/api/v1/inboxes/{inbox_id}/contacts/{source_id}/conversations")
    suspend fun listConversations(
        @Path("inbox_id") inboxIdentifier: String,
        @Path("source_id") sourceId: String,
    ): List<PublicConversationDto>

    @GET("public/api/v1/inboxes/{inbox_id}/contacts/{source_id}/conversations/{conversation_id}/messages")
    suspend fun listMessages(
        @Path("inbox_id") inboxIdentifier: String,
        @Path("source_id") sourceId: String,
        @Path("conversation_id") conversationId: Int,
    ): List<MessageDto>

    @POST("public/api/v1/inboxes/{inbox_id}/contacts/{source_id}/conversations/{conversation_id}/messages")
    suspend fun sendMessage(
        @Path("inbox_id") inboxIdentifier: String,
        @Path("source_id") sourceId: String,
        @Path("conversation_id") conversationId: Int,
        @Body body: SendMessageRequest,
    ): MessageDto

    /**
     * Bumps `contact_last_seen_at` server-side. The endpoint returns
     * `head :ok` with no body — Retrofit needs `Unit` for that to deserialize
     * cleanly. Verified in `ConversationsController#update_last_seen`.
     */
    @POST("public/api/v1/inboxes/{inbox_id}/contacts/{source_id}/conversations/{conversation_id}/update_last_seen")
    suspend fun updateLastSeen(
        @Path("inbox_id") inboxIdentifier: String,
        @Path("source_id") sourceId: String,
        @Path("conversation_id") conversationId: Int,
    )
}
