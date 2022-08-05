package com.baimsg.data.api

import java.time.Duration

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
object Config {
    const val BASE_URL: String = "https://note.youdao.com/"
    val API_TIMEOUT = Duration.ofSeconds(40).toMillis()
}