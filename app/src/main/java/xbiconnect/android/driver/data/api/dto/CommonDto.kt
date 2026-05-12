package xbiconnect.android.driver.data.api.dto

import com.google.gson.annotations.SerializedName

/** Unix timestamp in seconds (Chatwoot convention). */
typealias UnixTimestamp = Long

enum class ConversationStatus {
    @SerializedName("open") OPEN,
    @SerializedName("resolved") RESOLVED,
    @SerializedName("pending") PENDING,
    @SerializedName("snoozed") SNOOZED,
}

enum class MessageType(val value: Int) {
    @SerializedName("0") INCOMING(0),
    @SerializedName("1") OUTGOING(1),
    @SerializedName("2") ACTIVITY(2),
    @SerializedName("3") TEMPLATE(3),
}

enum class MessageStatus {
    @SerializedName("sent") SENT,
    @SerializedName("delivered") DELIVERED,
    @SerializedName("read") READ,
    @SerializedName("failed") FAILED,
    @SerializedName("progress") PROGRESS,
    @SerializedName("pending") PENDING,
}

enum class ContentType {
    @SerializedName("text") TEXT,
    @SerializedName("input_text") INPUT_TEXT,
    @SerializedName("input_select") INPUT_SELECT,
    @SerializedName("article") ARTICLE,
    @SerializedName("incoming_email") INCOMING_EMAIL,
    @SerializedName("integrations") INTEGRATIONS,
}

enum class Channel {
    @SerializedName("Channel::WebWidget") WEB_WIDGET,
    @SerializedName("Channel::FacebookPage") FACEBOOK_PAGE,
    @SerializedName("Channel::TwilioSms") TWILIO_SMS,
    @SerializedName("Channel::Whatsapp") WHATSAPP,
    @SerializedName("Channel::Sms") SMS,
    @SerializedName("Channel::Email") EMAIL,
    @SerializedName("Channel::Api") API,
}
