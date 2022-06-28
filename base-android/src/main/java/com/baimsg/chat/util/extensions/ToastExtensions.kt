package com.baimsg.chat.util.extensions

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import es.dmoral.toasty.Toasty

/**
 * Create by Baimsg on 2022/6/13
 *
 **/
fun Activity.showShort(msg: String) = applicationContext.showShort(msg)
fun Fragment.showShort(msg: String) = requireContext().showShort(msg)
fun Context.showShort(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Activity.showLong(msg: String) = applicationContext.showLong(msg)
fun Fragment.showLong(msg: String) = requireContext().showLong(msg)
fun Context.showLong(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun Activity.showInfo(msg: String) = applicationContext.showInfo(msg)
fun Fragment.showInfo(msg: String) = requireContext().showInfo(msg)
fun Context.showInfo(msg: String) {
    Toasty.info(this, msg, Toast.LENGTH_SHORT, true)
        .apply { show() }
}

fun Activity.showSuccess(msg: String) = applicationContext.showSuccess(msg)
fun Fragment.showSuccess(msg: String) = requireContext().showSuccess(msg)
fun Context.showSuccess(msg: String) {
    Toasty.success(this, msg, Toast.LENGTH_SHORT, true).apply {
        show()
    }
}

fun Activity.showWarning(msg: String) = applicationContext.showWarning(msg)
fun Fragment.showWarning(msg: String) = requireContext().showWarning(msg)
fun Context.showWarning(msg: String) {
    Toasty.warning(this, msg, Toast.LENGTH_SHORT, true).apply {
        show()
    }
}

fun Activity.showError(msg: String) = applicationContext.showError(msg)
fun Fragment.showError(msg: String) = requireContext().showError(msg)
fun Context.showError(msg: String) {
    Toasty.error(this, msg, Toast.LENGTH_SHORT, true).apply {
        show()
    }
}