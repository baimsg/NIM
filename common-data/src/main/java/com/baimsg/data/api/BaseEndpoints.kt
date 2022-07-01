package com.baimsg.data.api

import retrofit2.http.GET

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
interface BaseEndpoints {
    @GET("key")
    fun getKey(): String
}