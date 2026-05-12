package xbiconnect.android.driver.data.network

/**
 * Customer-scoped API. Base URL comes from the `url` field returned by
 * `validate-vin`; auth uses the `customer.api_token` from the same response.
 *
 * Endpoints will be added as the chat / announcements / campaigns features
 * get wired. For now this interface is a placeholder so the routing
 * infrastructure compiles and the instance URL gets persisted on pairing.
 */
interface XbiInstanceApi {
    // TODO: add conversations, messages, campaigns endpoints once the
    //  customer-scoped contract is defined.
}
