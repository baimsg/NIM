package com.baimsg.chat.util.extensions

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * Create by Baimsg on 2022/6/13
 *
 **/
fun Activity.showShort(msg: String) = applicationContext.showShort(msg)

fun Activity.showLong(msg: String) = applicationContext.showLong(msg)

fun Fragment.showShort(msg: String) = requireContext().showShort(msg)

fun Fragment.showLong(msg: String) = requireContext().showLong(msg)

fun Context.showShort(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.showLong(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}