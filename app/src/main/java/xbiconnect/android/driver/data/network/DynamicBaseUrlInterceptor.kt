package xbiconnect.android.driver.data.network

import android.util.Log
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import xbiconnect.android.driver.data.DriverPreferences

/**
 * Rewrites the request URL at fire time so all customer-scoped calls land on
 * the per-customer instance URL persisted in [DriverPreferences].
 *
 * Ported from the agent app's interceptor (same algorithm), adapted to:
 *  - Read the instance URL from the paired-vehicle snapshot instead of a
 *    standalone preference.
 *  - Use the `customer_id` returned by `validate-vin` as the account
 *    segment when building `/api/v1/accounts/{accountId}/...` paths.
 *
 * Routes that bypass the `accounts/{id}` prefix:
 *  - Anything under the `auth/...` namespace is sent verbatim to the host
 *    root (Chatwoot's devise endpoints, if we ever use them).
 *  - The known non-account routes (`profile`, `notification_subscriptions`,
 *    etc.) get only the `/api/v1/` prefix.
 *
 * Note: this is INERT until wired into an OkHttpClient. Today the Driver app
 * only talks to the cross-instance gateway (validate-vin, drivers-by-vin)
 * which goes through a separate client. This file lives here so that, the
 * moment the backend defines the customer-scoped auth flow, plugging it in
 * is a one-line change.
 */
class DriverDynamicBaseUrlInterceptor(
    private val prefs: DriverPreferences,
) : Interceptor {

    companion object {
        private const val TAG = "DriverDynBaseUrl"
        private val NON_ACCOUNT_ROUTES = listOf(
            "profile",
            "profile/availability",
            "notification_subscriptions",
            "profile/set_active_account",
            "xbi_config",
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalUrl = original.url

        val paired = runBlocking { prefs.pairedVehicle.first() }
        val instanceUrl = paired?.instanceUrl
        val accountId = paired?.customerId

        if (instanceUrl.isNullOrBlank()) {
            // No pairing → we can't rewrite; let the request fly as-is so the
            // caller sees a sensible network error rather than a silent crash.
            Log.w(TAG, "No instance URL; passing request through: $originalUrl")
            return chain.proceed(original)
        }

        val effectiveBaseUrl = when {
            instanceUrl.startsWith("http://") || instanceUrl.startsWith("https://") -> instanceUrl
            else -> "https://$instanceUrl"
        }.trimEnd('/')

        val newBaseUrl = effectiveBaseUrl.toHttpUrlOrNull()
        if (newBaseUrl == null) {
            Log.e(TAG, "Failed to parse instance URL: $effectiveBaseUrl")
            return chain.proceed(original)
        }

        val pathSegments = originalUrl.pathSegments
        val firstSegment = pathSegments.firstOrNull().orEmpty()

        val isAuthRoute = firstSegment == "auth"
        val isNonAccountRoute = NON_ACCOUNT_ROUTES.any { it.startsWith(firstSegment) }

        val newSegments = mutableListOf<String>()
        when {
            isAuthRoute -> newSegments.addAll(pathSegments)
            isNonAccountRoute -> {
                newSegments += "api"
                newSegments += "v1"
                newSegments.addAll(pathSegments)
            }
            accountId != null && accountId > 0L -> {
                newSegments += "api"
                newSegments += "v1"
                newSegments += "accounts"
                newSegments += accountId.toString()
                newSegments.addAll(pathSegments)
            }
            else -> {
                Log.w(TAG, "No accountId; falling back to /api/v1/${pathSegments.joinToString("/")}")
                newSegments += "api"
                newSegments += "v1"
                newSegments.addAll(pathSegments)
            }
        }

        val newUrl = newBaseUrl.newBuilder()
            .apply {
                newSegments.forEach { addPathSegment(it) }
                originalUrl.queryParameterNames.forEach { name ->
                    originalUrl.queryParameter(name)?.let { value ->
                        addQueryParameter(name, value)
                    }
                }
            }
            .build()

        val rewritten = original.newBuilder().url(newUrl).build()
        return chain.proceed(rewritten)
    }
}
