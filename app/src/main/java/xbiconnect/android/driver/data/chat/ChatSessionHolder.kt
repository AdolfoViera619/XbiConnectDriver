package xbiconnect.android.driver.data.chat

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.UUID
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import xbiconnect.android.driver.data.Resource
import xbiconnect.android.driver.data.api.dto.MessageDto
import xbiconnect.android.driver.data.api.dto.MessageType
import xbiconnect.android.driver.data.network.DriverActionCableService
import xbiconnect.android.driver.data.network.DriverWsEvent
import xbiconnect.android.driver.data.repository.ChatwootRepository
import xbiconnect.android.driver.data.state.ChatState

/**
 * Session-scoped holder for the driver chat. Lives at the AppShell level so
 * it persists across tab switches, owns the WebSocket lifecycle, and exposes
 * an [unreadCount] that drives the sidebar badge.
 *
 * Flow:
 *  1. [start] kicks off `findOrCreateContact` → `loadOrStartConversation` →
 *     `listMessages` against [repo], populating [state].
 *  2. Once the contact is loaded the WebSocket subscribes to its `RoomChannel`;
 *     incoming `message.created` events for the active conversation are
 *     appended to [state] (with echo-id reconciliation for our own
 *     optimistic sends).
 *  3. [send] does an optimistic append + REST POST + echo reconciliation.
 *  4. [markRead] bumps `contact_last_seen_at` server-side and locally so
 *     [unreadCount] drops to 0 immediately.
 *  5. [dispose] tears down the WebSocket; called when the AppShell leaves
 *     composition (unlink, etc.).
 */
class ChatSessionHolder(
    private val scope: CoroutineScope,
    private val repo: ChatwootRepository,
    private val sourceId: String,
    private val contactName: String?,
    private val contactAttributes: Map<String, Any?>?,
    private val wsBaseUrl: String,
) {

    private val ws = DriverActionCableService()
    private val started = AtomicBoolean(false)
    private var wsCollectJob: Job? = null
    private var disposed = false

    private var _state by mutableStateOf<ChatState>(ChatState.Idle)
    val state: ChatState get() = _state

    /**
     * Outgoing-from-agent messages that landed after the truck's last seen
     * timestamp. Driving signal for the sidebar badge.
     */
    private val unreadCountState by derivedStateOf {
        val ready = _state as? ChatState.Ready ?: return@derivedStateOf 0
        val seenAt = ready.conversation.contactLastSeenAt ?: 0L
        ready.messages.count { m ->
            m.messageType == MessageType.OUTGOING && m.createdAt > seenAt
        }
    }
    val unreadCount: Int get() = unreadCountState

    fun start() {
        if (disposed) return
        if (!started.compareAndSet(false, true)) return
        scope.launch { initialLoad() }
    }

    fun dispose() {
        if (disposed) return
        disposed = true
        wsCollectJob?.cancel()
        ws.disconnect()
    }

    fun send(text: String) {
        val ready = _state as? ChatState.Ready ?: return
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        val echoId = UUID.randomUUID().toString()
        val optimistic = MessageDto(
            id = 0,
            content = trimmed,
            messageType = MessageType.INCOMING, // public channel: from contact = from truck
            createdAt = System.currentTimeMillis() / 1000,
            conversationId = ready.conversation.id,
            echoId = echoId,
        )
        _state = ready.copy(messages = ready.messages + optimistic, sending = true)

        scope.launch {
            when (val r = repo.sendMessage(sourceId, ready.conversation.id, trimmed, echoId)) {
                is Resource.Success -> {
                    val current = _state as? ChatState.Ready ?: return@launch
                    _state = current.copy(
                        messages = current.messages.map { m ->
                            if (m.echoId != null && m.echoId == echoId) r.data else m
                        },
                        sending = false,
                    )
                }
                is Resource.Error -> {
                    val current = _state as? ChatState.Ready ?: return@launch
                    _state = current.copy(sending = false)
                    // Optimistic message stays visible so the user knows what failed.
                }
                else -> Unit
            }
        }
    }

    fun markRead() {
        val ready = _state as? ChatState.Ready ?: return
        val now = System.currentTimeMillis() / 1000
        // Local update so the badge drops to 0 immediately.
        _state = ready.copy(
            conversation = ready.conversation.copy(contactLastSeenAt = now),
        )
        // Best-effort server sync.
        scope.launch { repo.markAsRead(sourceId, ready.conversation.id) }
    }

    private suspend fun initialLoad() {
        _state = ChatState.Loading

        val contact = when (val r = repo.findOrCreateContact(sourceId, contactName, contactAttributes)) {
            is Resource.Success -> r.data
            is Resource.Error -> { _state = ChatState.Error(r.message); return }
            else -> return
        }
        val convo = when (val r = repo.loadOrStartConversation(sourceId)) {
            is Resource.Success -> r.data
            is Resource.Error -> { _state = ChatState.Error(r.message); return }
            else -> return
        }
        val messages = when (val r = repo.listMessages(sourceId, convo.id)) {
            is Resource.Success -> r.data.sortedBy { it.createdAt }
            is Resource.Error -> { _state = ChatState.Error(r.message); return }
            else -> return
        }
        _state = ChatState.Ready(contact, convo, messages)

        // Wire WebSocket once we have the pubsub_token.
        val token = contact.pubsubToken ?: return
        ws.connect(wsBaseUrl, token)
        wsCollectJob = scope.launch {
            ws.events.collect { event -> handleWsEvent(event) }
        }
    }

    private fun handleWsEvent(event: DriverWsEvent) {
        val ready = _state as? ChatState.Ready ?: return
        when (event) {
            is DriverWsEvent.MessageReceived -> {
                if (event.conversationId != ready.conversation.id) return
                _state = mergeIncomingMessage(ready, event.message)
            }
            is DriverWsEvent.MessageUpdated -> {
                if (event.conversationId != ready.conversation.id) return
                _state = ready.copy(
                    messages = ready.messages.map { m ->
                        if (m.id != 0 && m.id == event.message.id) event.message else m
                    },
                )
            }
            DriverWsEvent.Connected,
            DriverWsEvent.Disconnected,
            is DriverWsEvent.Error -> Unit
        }
    }

    /**
     * Adds a message coming in over the wire while respecting two dedup keys:
     * - `id` (server-assigned) for things the wire delivers twice
     * - `echo_id` (client-assigned) so our optimistic copy gets replaced by
     *    the canonical server version rather than duplicated
     */
    private fun mergeIncomingMessage(ready: ChatState.Ready, incoming: MessageDto): ChatState.Ready {
        val byEcho = incoming.echoId?.takeIf { it.isNotBlank() }
        val matchedOptimistic = byEcho != null && ready.messages.any { it.echoId == byEcho }
        if (matchedOptimistic) {
            return ready.copy(
                messages = ready.messages.map { m -> if (m.echoId == byEcho) incoming else m },
            )
        }
        if (ready.messages.any { it.id != 0 && it.id == incoming.id }) {
            return ready // already there
        }
        return ready.copy(messages = ready.messages + incoming)
    }
}
