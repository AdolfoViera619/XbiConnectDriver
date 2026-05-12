package xbiconnect.android.driver.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xbiconnect.android.driver.BuildConfig
import java.util.concurrent.TimeUnit

/**
 * Builds Retrofit clients. There are two distinct base URLs in this product:
 *
 * 1. The **main** XBI API (`api-xbi-main.xbiplus.com`) — endpoints that operate
 *    across all customer instances: VIN pairing, HOS proxy, etc. This client
 *    is a singleton.
 *
 * 2. The **instance** URL returned by `validate-vin` — different for each
 *    customer. After pairing, all customer-scoped traffic (messages,
 *    conversations, campaigns) flows through that URL with the
 *    `customer.api_token` for auth. Build one on demand via
 *    [forInstance] and cache it as long as the URL doesn't change.
 */
object ApiClient {

    private const val MAIN_BASE_URL = "https://api-xbi-main.xbiplus.com/"

    private val sharedOkHttp: OkHttpClient by lazy { buildOkHttp(authToken = null) }

    val mainApi: XbiMainApi by lazy {
        Retrofit.Builder()
            .baseUrl(MAIN_BASE_URL)
            .client(sharedOkHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(XbiMainApi::class.java)
    }

    /**
     * Returns an [XbiInstanceApi] bound to the given customer instance URL.
     * Caller is responsible for caching this — typically you'd hold it as long
     * as the paired instance doesn't change.
     */
    fun forInstance(baseUrl: String, authToken: String?): XbiInstanceApi {
        val normalized = baseUrl.trimEnd('/') + "/"
        return Retrofit.Builder()
            .baseUrl(normalized)
            .client(buildOkHttp(authToken))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(XbiInstanceApi::class.java)
    }

    private fun buildOkHttp(authToken: String?): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                }
                if (!authToken.isNullOrBlank()) {
                    addInterceptor { chain ->
                        val req = chain.request().newBuilder()
                            .header("Authorization", "Bearer $authToken")
                            .build()
                        chain.proceed(req)
                    }
                }
            }
            .build()
}
