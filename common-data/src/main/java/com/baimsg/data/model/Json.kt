package com.baimsg.data.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

/**
 * Create by Baimsg on 2022/6/20
 *
 **/
@OptIn(ExperimentalSerializationApi::class)
val DEFAULT_JSON_FORMAT = Json {
    prettyPrint = true//格式化json
    prettyPrintIndent = "    "
    isLenient = true //宽松解析
    ignoreUnknownKeys = true //忽略未知键
    encodeDefaults = true //默认值参与序列化
    coerceInputValues = true //值类型和实例化对象类型不一致时，使用对象的默认值
    explicitNulls = false  //忽略null
}

val JSON = DEFAULT_JSON_FORMAT