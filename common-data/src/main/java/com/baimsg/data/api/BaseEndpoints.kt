package com.baimsg.data.api

import com.baimsg.data.model.BaseConfig
import retrofit2.http.*

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
interface BaseEndpoints {
    @GET("key")
    suspend fun getKey(
        @Header(NetConfig.DYNAMIC_URL) baseUrl: String = NetConfig.THIRD_PARTY_URL,
    ): BaseConfig

    /**
     * @param baseUrl API地址
     * @param uid 用户id
     * @param sign appKey
     * @param timestamp 时间戳
     */
    @FormUrlEncoded
    @POST("api/user/detail")
    suspend fun postUserDetail(
        @HeaderMap headers: Map<String, String>,
        @FieldMap fields: Map<String, String>
    ): String
}