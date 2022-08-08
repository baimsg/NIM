package com.baimsg.data.api

import com.baimsg.data.model.ConfigBean
import retrofit2.http.*

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
interface BaseEndpoints {
    /**
     * 有道云笔记获取配置信息
     * @param fileId 文件id
     * @param shareKey 分享密钥
     * @param method 请求方法
     * @param read 是否可读性
     */
    @GET("yws/api/personal/file/{fd}")
    suspend fun getPersonal(
        @Path("fd") fileId: String,
        @Query("shareKey") shareKey: String,
        @Query("method") method: String = "read",
        @Query("read") read: Boolean = true
    ): ConfigBean

    /**
     * @param baseUrl API地址
     * @param uid 用户id
     * @param sign appKey
     * @param timestamp 时间戳
     */
    @FormUrlEncoded
    @POST("/api/user/getFakeUid")
    suspend fun postUserDetail(
        @HeaderMap headers: Map<String, String>,
        @FieldMap fields: Map<String, String>
    ): String
}