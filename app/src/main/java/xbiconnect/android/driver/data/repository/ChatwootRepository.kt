package xbiconnect.android.driver.data.repository

import xbiconnect.android.driver.data.Resource
import xbiconnect.android.driver.data.api.dto.CreateContactRequest
import xbiconnect.android.driver.data.api.dto.MessageDto
import xbiconnect.android.driver.data.api.dto.PublicContactDto
import xbiconnect.android.driver.data.api.dto.PublicConversationDto
import xbiconnect.android.driver.data.api.dto.SendMessageRequest
import xbiconnect.android.driver.data.network.XbiInstanceApi
import java.io.IOException

/**
 * Wraps the Chatwoot Channel::Api public endpoints. One repository covers
 * the whole driver-chat surface because the operations are tightly coupled
 * (you almost always do contact + conversation + messages in the same flow).
 *
 * The repo carries the `inboxIdentifier` so call sites don't repeat it; pass
 * the VIN as `sourceId` since that's the convention we agreed with the
 * Mediador (see msg 003 in `Tasks/para-driver-app/`).
 */
class ChatwootRepository(
    private val api: XbiInstanceApi,
    private val inboxIdentifier: String,
) {

    /**
     * Get-or-create the contact for [sourceId] (VIN). Chatwoot's
     * `POST /contacts` is idempotent on `source_id` server-side
     * (`ContactInboxWithContactBuilder` calls `find_by(source_id:)` first
     * and returns the existing record if any) — so a single POST handles
     * both cases without a probe GET that would 404 the first time.
     */
    suspend fun findOrCreateContact(
        sourceId: String,
        displayName: String?,
        customAttributes: Map<String, Any?>? = null,
    ): Resource<PublicContactDto> = try {
        val contact = api.createContact(
            inboxIdentifier = inboxIdentifier,
            body = CreateContactRequest(
                sourceId = sourceId,
                identifier = sourceId,
                name = displayName,
                customAttributes = customAttributes,
            ),
        )
        Resource.Success(contact)
    } catch (e: IOException) {
        Resource.Error("Sin conexión con el servidor de mensajes.", e)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Error al obtener el contacto.", e)
    }

    /**
     * Return the contact's conversations. Creates one on the fly if the
     * list comes back empty so the tablet always has somewhere to write.
     */
    suspend fun loadOrStartConversation(sourceId: String): Resource<PublicConversationDto> = try {
        val list = api.listConversations(inboxIdentifier, sourceId)
        val conversation = list.firstOrNull()
            ?: api.createConversation(inboxIdentifier, sourceId)
        Resource.Success(conversation)
    } catch (e: IOException) {
        Resource.Error("Sin conexión con el servidor de mensajes.", e)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Error al iniciar la conversación.", e)
    }

    suspend fun listMessages(
        sourceId: String,
        conversationId: Int,
    ): Resource<List<MessageDto>> = try {
        Resource.Success(api.listMessages(inboxIdentifier, sourceId, conversationId))
    } catch (e: IOException) {
        Resource.Error("Sin conexión con el servidor de mensajes.", e)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Error al leer mensajes.", e)
    }

    suspend fun sendMessage(
        sourceId: String,
        conversationId: Int,
        content: String,
        echoId: String? = null,
    ): Resource<MessageDto> = try {
        val msg = api.sendMessage(
            inboxIdentifier = inboxIdentifier,
            sourceId = sourceId,
            conversationId = conversationId,
            body = SendMessageRequest(content = content, echoId = echoId),
        )
        Resource.Success(msg)
    } catch (e: IOException) {
        Resource.Error("Sin conexión al enviar el mensaje.", e)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "No se pudo enviar el mensaje.", e)
    }

    /**
     * Bumps `contact_last_seen_at` server-side so the unread counter resets
     * across devices. Best-effort: failures don't propagate, since losing
     * one read receipt is a minor inconsistency, not worth interrupting UI.
     */
    suspend fun markAsRead(sourceId: String, conversationId: Int): Resource<Unit> = try {
        api.updateLastSeen(inboxIdentifier, sourceId, conversationId)
        Resource.Success(Unit)
    } catch (e: Exception) {
        Resource.Error(e.message ?: "No se pudo actualizar last_seen.", e)
    }
}
