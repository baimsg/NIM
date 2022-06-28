package com.baimsg.chat.util.extensions

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.Fragment
import es.dmoral.toasty.Toasty

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


fun Activity.showInfo(msg: String) = applicationContext::showInfo::invoke

fun Fragment.showInfo(msg: String) = requireContext()::showInfo

fun Context.showInfo(msg: String) {
    listOf("1","3","3").map { this::showInfo }
    Toasty.info(this, msg, Toast.LENGTH_SHORT, true)
        .apply { show() }
}

fun Fragment.showSuccess(msg: String) {
    Toasty.success(requireContext(), msg, Toast.LENGTH_SHORT, true).apply {
        show()
    }
}

fun Fragment.showWarning(msg: String) {
    Toasty.warning(requireContext(), msg, Toast.LENGTH_SHORT, true).apply {
        show()
    }
}

fun Fragment.showError(msg: String) {
    Toasty.error(requireContext(), msg, Toast.LENGTH_SHORT, true).apply {
        show()
    }
}