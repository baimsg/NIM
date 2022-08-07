package com.baimsg.chat.util.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri

/**
 * Create by Baimsg on 2022/8/6
 *
 **/
fun Activity.openWeb(url: String) {
    startActivity(Intent().apply {
        action = "android.intent.action.VIEW"
        data = Uri.parse(url)
    })
}