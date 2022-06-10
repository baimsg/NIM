package com.baimsg.decide

import android.content.Context

/**
 * Create by Baimsg on 2022/6/10
 *
 **/

fun Context.dip2px(dpValue: Float): Int {
    val scale = resources.displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}
