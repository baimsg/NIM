package com.baimsg.data.api

import com.baimsg.data.model.BaseConfig
import retrofit2.http.GET
import retrofit2.http.Header

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
interface BaseEndpoints {
    @GET("key")
    suspend fun getKey(
        @Header(NetConfig.DYNAMIC_URL) baseUrl: String = NetConfig.THIRD_PARTY_URL,
    ): BaseConfig

    @GET("")
    suspend fun userDetail()
}