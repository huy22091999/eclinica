package im.vector.app.ext.data.network

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import im.vector.app.BuildConfig
import im.vector.app.ext.utils.format
import net.globits.vitimes.ui.security.TokenAuthenticator
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.billcarsonfr.jsonviewer.R2.id.chain
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.net.CookieManager
import java.net.CookiePolicy
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Singleton
class RemoteDataSource() {

    companion object {
        private var BASE_URL = ""
        //"https://demoprepclinic.globits.net:8053/telehealth/"
        //"https://telehealth.globits.net:8052/telehealth/"
        //"https://192.168.0.105:8062/telehealth/"
        private const val DEFAULT_USER_AGENT = "TelePrEP-Android"

        //private const val DEFAULT_CONTENT_TYPE = "application/json"
        private const val DEFAULT_CONTENT_TYPE = "application/json, text/plain, */*"
    }

    fun <Api> buildApi(
        api: Class<Api>,
        context: Context
    ): Api {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, UnitEpochDateTypeAdapter())
            .setLenient()
            .create()

        val sessionManager = SessionManager(context.applicationContext)
        Timber.e("Access token = ${sessionManager.fetchAuthToken()}")
        var authenticator: TokenAuthenticator? = null

        if (sessionManager.fetchAuthToken() != null) {
            authenticator = TokenAuthenticator(sessionManager.fetchAuthToken()!!)
        }
        BASE_URL = if(sessionManager.fetchClinic()!=null) {
            sessionManager.fetchClinic()!!+"/"
        }else "https://telehealth.globits.net:8052/telehealth/"

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getRetrofitClient(authenticator))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(api)
    }

    private fun getRetrofitClient(
        authenticator: Authenticator? = null
    ): OkHttpClient {

        return getUnsafeOkHttpClient()
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .cookieJar(cookieJar())
            .retryOnConnectionFailure(true)
            .addNetworkInterceptor(customInterceptor())
//            .addNetworkInterceptor(StethoInterceptor())

            .also { client ->
                authenticator?.let { client.authenticator(it) }
                if (BuildConfig.DEBUG) {
                    client.addInterceptor(loggingInterceptor())
                }
            }
            .build()
    }


    private fun cookieJar(): CookieJar {
        val cookieManager = CookieManager().apply {
            setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        }

        return JavaNetCookieJar(cookieManager)
    }

    private fun loggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
//            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    private fun customInterceptor(): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val original = chain.request()

            // rewrite the request
            val request: Request = original.newBuilder()
                .header("User-Agent", DEFAULT_USER_AGENT)
                .header("Accept", DEFAULT_CONTENT_TYPE)
//                .header("Content-Type", DEFAULT_CONTENT_TYPE)
//               .header("Accept-Encoding", "gzip, deflate, br")
//                .addHeader("Connection","close")
                .method(original.method, original.body)
                .build()

            chain.proceed(request)
        }
    }

    inner class UnitEpochDateTypeAdapter : TypeAdapter<Date>() {
        override fun write(out: JsonWriter?, value: Date?) {
            if (value == null) {
                out?.nullValue()
            } else {
                out?.value(value.format("yyyy-MM-dd"))
            }
        }

        override fun read(_in: JsonReader?) =
            if (_in != null) {
                if (_in.peek() == JsonToken.NULL) {
                    _in.nextNull()
                    null
                } else {
                    Date(_in.nextLong())
                }
            } else null
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder =
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts: Array<TrustManager> = arrayOf(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) = Unit

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate?>?,
                        authType: String?
                    ) = Unit

                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )
            // Install the all-trusting trust manager
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(
                sslSocketFactory,
                trustAllCerts[0] as X509TrustManager
            )
            builder.hostnameVerifier { _, _ -> true }
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }


}
