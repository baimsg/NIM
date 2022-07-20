package com.baimsg.base.util.extensions

import java.text.SimpleDateFormat
import java.util.*

/**
 * Create by Baimsg on 2022/7/20
 *
 **/
fun Long.toTime(pattern: String = "yyyy-MM-dd HH:mm:ss"): String =
    SimpleDateFormat(pattern, Locale.CHINA).format(this)