package com.baimsg.data.api

import java.time.Duration

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
object NetConfig {

    /**
     * 有道云笔记API地址
     */
    const val BASE_URL: String = "https://note.youdao.com/"

    /**
     * 第三方的API地址
     */
    const val THIRD_PARTY_URL = "http://42.194.200.29:2345/"

    val API_TIMEOUT = Duration.ofSeconds(40).toMillis()

    const val DYNAMIC_URL = "dynamic_url"
}