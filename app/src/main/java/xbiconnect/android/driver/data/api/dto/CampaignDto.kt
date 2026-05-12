package xbiconnect.android.driver.data.api.dto

import com.google.gson.annotations.SerializedName

/**
 * Chatwoot Campaign — used for fleet-wide announcements (Comunicados).
 *
 * Per the architecture notes in `claude_init.txt`:
 * - `one_off` for puntual announcements (the "anclado" / urgente cards).
 * - `ongoing` for trigger-based automatic messages.
 *
 * Exact shape needs confirmation against a real Chatwoot response — these
 * fields follow the public Chatwoot API docs and may need adjustment when
 * the actual response is observed.
 */
data class CampaignDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("campaign_type") val campaignType: String?,   // "one_off" | "ongoing"
    @SerializedName("campaign_status") val campaignStatus: String?, // "active" | "completed"
    @SerializedName("scheduled_at") val scheduledAt: String?,
    @SerializedName("inbox") val inbox: InboxDto?,
    @SerializedName("sender") val sender: UserDto?,
    @SerializedName("audience") val audience: List<Map<String, Any?>>? = null,
)

data class CampaignsResponse(
    @SerializedName("payload") val payload: List<CampaignDto> = emptyList(),
)
