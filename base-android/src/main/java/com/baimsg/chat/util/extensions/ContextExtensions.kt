package com.baimsg.chat.util.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

fun Context.copy(message: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText("simple text", message)
    clipboard.setPrimaryClip(clip)
}