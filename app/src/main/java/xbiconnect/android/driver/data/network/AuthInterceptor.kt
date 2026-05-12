package xbiconnect.android.driver.data.network

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import xbiconnect.android.driver.data.DriverPreferences

/**
 * Attaches the customer-scoped auth credential to outgoing requests, plus a
 * standard `Accept` header.
 *
 * ⚠️ **Auth scheme TBD.** We verified empirically that the `customer.api_token`
 * returned by `validate-vin` does NOT authenticate against Chatwoot via any
 * of the common header forms (`api_access_token`, `Authorization: Bearer`,
 * devise `access-token`). The backend team needs to define how a tablet
 * authenticates against the customer instance — see the project memory note
 * `Driver — Chatwoot auth gap`.
 *
 * Until that's resolved, this interceptor sends `api_access_token: <token>`
 * as the best-guess header. When the real scheme is known, swap the body of
 * [applyAuth] and nothing else changes.
 */
class DriverAuthInterceptor(
    private val prefs: DriverPreferences,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
            .addHeader("Accept", "application/json, text/plain, */*")

        val token = runBlocking { prefs.pairedVehicle.first()?.apiToken }
        if (!token.isNullOrBlank()) {
            applyAuth(builder, token)
        }

        return chain.proceed(builder.build())
    }

    private fun applyAuth(builder: okhttp3.Request.Builder, token: String) {
        // Best-guess Chatwoot per-user API key header. If the backend ends up
        // using devise_token_auth (access-token / uid / client) or a custom
        // header, replace this with the right combination here.
        builder.addHeader("api_access_token", token)
    }
}
