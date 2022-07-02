package com.baimsg.data.api

import com.baimsg.data.model.BaseConfig
import retrofit2.http.GET

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
interface BaseEndpoints {
    @GET("key")
   suspend fun getKey(): BaseConfig
}