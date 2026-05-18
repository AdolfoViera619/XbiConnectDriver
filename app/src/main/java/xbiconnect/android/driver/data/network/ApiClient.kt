package xbiconnect.android.driver.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xbiconnect.android.driver.BuildConfig
import xbiconnect.android.driver.data.DriverConfig
import java.util.concurrent.TimeUnit

/**
 * Builds Retrofit clients for the Driver app.
 *
 * Two distinct surfaces:
 *
 * 1. **Gateway** ([XbiMainApi]): hosts the cross-instance XBI endpoints
 *    (`validate-vin`, `drivers-by-vin`). Base URL is fixed to
 *    [DriverConfig.GATEWAY_BASE_URL]. No local docker version exists — the
 *    gateway is production-only. Singleton.
 *
 * 2. **Chatwoot instance** ([XbiInstanceApi]): the customer's Chatwoot
 *    workspace, where conversations live. Base URL changes with
 *    [xbiconnect.android.driver.data.ServerMode] (LOCAL → docker; PROD →
 *    instance URL from the paired vehicle). Public Channel::Api endpoints
 *    don't require auth — only the inbox identifier in the URL.
 *
 * The instance client is rebuilt when the base URL changes (rare:
 * pair / unpair / mode toggle). Callers ask for it via [instanceApi].
 */
object ApiClient {

    private val sharedLogging by lazy {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    private val gatewayOkHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(sharedLogging)
            .build()
    }

    val mainApi: XbiMainApi by lazy {
        Retrofit.Builder()
            .baseUrl(DriverConfig.GATEWAY_BASE_URL)
            .client(gatewayOkHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(XbiMainApi::class.java)
    }

    @Volatile private var cachedInstanceUrl: String? = null
    @Volatile private var cachedInstanceApi: XbiInstanceApi? = null

    /**
     * Returns an [XbiInstanceApi] bound to [baseUrl]. The Retrofit instance
     * is cached and reused while the URL is the same; rebuilt automatically
     * when the user toggles server mode or re-pairs.
     */
    @Synchronized
    fun instanceApi(baseUrl: String): XbiInstanceApi {
        val normalized = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        cachedInstanceApi?.takeIf { cachedInstanceUrl == normalized }?.let { return it }

        val okHttp = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(sharedLogging)
            .build()

        val api = Retrofit.Builder()
            .baseUrl(normalized)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(XbiInstanceApi::class.java)

        cachedInstanceUrl = normalized
        cachedInstanceApi = api
        return api
    }
}

