package xbiconnect.android.driver.data.state

import xbiconnect.android.driver.data.api.dto.MessageDto
import xbiconnect.android.driver.data.api.dto.PublicContactDto
import xbiconnect.android.driver.data.api.dto.PublicConversationDto

/** UI state of the chat screen against the real Chatwoot backend. */
sealed interface ChatState {
    data object Idle : ChatState
    data object Loading : ChatState
    data class Ready(
        val contact: PublicContactDto,
        val conversation: PublicConversationDto,
        val messages: List<MessageDto>,
        val sending: Boolean = false,
    ) : ChatState

    data class Error(val message: String) : ChatState
}
