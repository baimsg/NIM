package com.baimsg.data.api.interceptor

import com.baimsg.data.api.NetConfig
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Create by Baimsg on 2022/8/6
 *
 **/
class DynamicURLInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        val baseUrls = request.headers(NetConfig.DYNAMIC_URL)
        return if (baseUrls.isNotEmpty()) {
            builder.removeHeader(NetConfig.DYNAMIC_URL)
            baseUrls[0].toHttpUrlOrNull()?.let { baseUrl ->
                return chain.proceed(
                    builder.url(
                        request.url.run {
                            newBuilder().scheme(baseUrl.scheme)
                                .host(baseUrl.host)
                                .port(baseUrl.port)
                                .build()
                        }
                    ).build()
                )
            }
            chain.proceed(request)
        } else {
            chain.proceed(request)
        }
    }
}