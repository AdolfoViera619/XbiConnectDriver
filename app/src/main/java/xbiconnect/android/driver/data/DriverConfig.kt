package xbiconnect.android.driver.data

/**
 * Static configuration for the Driver app's network layer.
 *
 * The two URLs the app talks to are intentionally separated:
 *
 * - **Gateway** (`api-xbi-main.xbiplus.com`) — hosts the cross-instance XBI
 *   endpoints (`validate-vin`, `drivers-by-vin`). There is no local docker
 *   version of the gateway, so this URL is fixed regardless of [ServerMode].
 *
 * - **Chatwoot instance** — where conversations / messages / contacts live.
 *   For [ServerMode.LOCAL] this points at the developer's docker; for
 *   [ServerMode.PROD] it should come from `validate-vin`'s `url` field
 *   (persisted into [PairedVehicle.instanceUrl]) and falls back to
 *   `connect.xbiplus.com` if missing.
 */
object DriverConfig {

    const val GATEWAY_BASE_URL = "https://api-xbi-main.xbiplus.com/"

    /** Default Chatwoot Channel::Api inbox identifier used while developing
     *  against the local docker. Verified against inbox 32 ("Drivers") in
     *  the dev Rails (see Mediador msg 003). Replace with whatever the
     *  gateway returns once `validate-vin` is extended. */
    const val DEV_INBOX_IDENTIFIER = "Rgc831KThuw3PMkj9ph8Ekk2"

    /** Local Chatwoot docker URL. IP may shift with DHCP; see Mediador
     *  msg 002. */
    const val LOCAL_CHATWOOT_URL = "http://192.168.100.8:3001/"

    /** Production Chatwoot fallback if the paired-vehicle snapshot doesn't
     *  carry an `instanceUrl`. */
    const val PROD_CHATWOOT_FALLBACK = "https://connect.xbiplus.com/"

    /**
     * Resolves which Chatwoot base URL the customer-scoped traffic should
     * hit, given the current toggle and pairing state.
     */
    fun resolveInstanceUrl(mode: ServerMode, paired: PairedVehicle?): String =
        when (mode) {
            ServerMode.LOCAL -> LOCAL_CHATWOOT_URL
            ServerMode.PROD -> paired?.instanceUrl?.takeIf { it.isNotBlank() }
                ?: PROD_CHATWOOT_FALLBACK
        }

    /**
     * Resolves the Chatwoot inbox identifier. In dev mode we fall back to
     * the hardcoded one so the chat works without a gateway extension; in
     * prod we strictly use whatever the gateway persisted.
     */
    fun resolveInboxIdentifier(mode: ServerMode, paired: PairedVehicle?): String? =
        paired?.inboxIdentifier?.takeIf { it.isNotBlank() }
            ?: if (mode == ServerMode.LOCAL) DEV_INBOX_IDENTIFIER else null
}
