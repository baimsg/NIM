package com.baimsg.data.api

import android.app.Application
import com.baimsg.data.BuildConfig
import com.baimsg.data.api.converter.ToStringConverterFactory
import com.baimsg.data.model.DEFAULT_JSON_FORMAT
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.File
import java.io.IOException
import java.net.Proxy
import java.net.ProxySelector
import java.net.SocketAddress
import java.net.URI
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    private fun getBaseBuilder(cache: Cache): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .sslSocketFactory(
                sslSocketFactory = SSLSocketClient.getSSLSocketFactory(),
                trustManager = SSLSocketClient.X509TrustManager
            )
            .proxySelector(object : ProxySelector() {
                override fun select(uri: URI?): MutableList<Proxy> {
                    return Collections.singletonList(Proxy.NO_PROXY)
                }

                override fun connectFailed(uri: URI?, sa: SocketAddress?, ioe: IOException?) = Unit
            })
            .hostnameVerifier { _, _ -> true }
            .cache(cache)
            .connectTimeout(Config.API_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Config.API_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(Config.API_TIMEOUT, TimeUnit.MILLISECONDS)
            .connectionPool(ConnectionPool(10, 2, TimeUnit.MINUTES))
            .dispatcher(
                Dispatcher().apply {
                    // 允许在同一主机上增加并发图像获取的数量
                    maxRequestsPerHost = 10
                }
            )
            .retryOnConnectionFailure(true)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun createRetrofit(baseUrl: String, client: OkHttpClient, json: Json) =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(ToStringConverterFactory())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(client)
            .build()

    @Provides
    @Singleton
    fun okHttpCache(app: Application): Cache =
        Cache(File(app.cacheDir, "api_cache"), (30 * 1024 * 1024).toLong())

    @Provides
    @Singleton
    fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC
        return interceptor
    }

    @Provides
    @Singleton
    fun okHttp(
        cache: Cache,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient = getBaseBuilder(cache)
        .addInterceptor(loggingInterceptor)
        .build()

    @Provides
    @Singleton
    fun jsonConfigured(): Json = DEFAULT_JSON_FORMAT

    @Provides
    @Singleton
    @ExperimentalSerializationApi
    @Named("base")
    fun retrofitBase(client: OkHttpClient, json: Json): Retrofit =
        createRetrofit(Config.BASE_URL, client, json)
}