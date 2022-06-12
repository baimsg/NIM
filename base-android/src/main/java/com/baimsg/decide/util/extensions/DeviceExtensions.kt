package com.baimsg.decide.util.extensions

import android.content.Context
import com.baimsg.decide.util.DeviceHelper

/**
 * 是否是平板
 */
fun Context.isTablet() = DeviceHelper.isTablet(this)

/**
 * 是否是小米全面屏
 */
fun Context.isMiuiFullDisplay() = DeviceHelper.isMiuiFullDisplay(this)

/**
 * 获取运行内存
 */
fun Context.getTotalMemory() = DeviceHelper.getTotalMemory(this)
