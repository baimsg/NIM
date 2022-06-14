package com.baimsg.chat.util.extensions

import android.app.Activity
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.baimsg.chat.util.KeyboardHelper

fun EditText.showKeyboard(delay: Boolean = true) = KeyboardHelper.showKeyboard(this, delay)

fun EditText.showKeyboard(delay: Int = 200) = KeyboardHelper.showKeyboard(this, delay)

fun View.hideKeyboard() = KeyboardHelper.hideKeyboard(this)

fun Activity.setVisibilityEventListener(
    listener: KeyboardHelper.KeyboardVisibilityEventListener
) = KeyboardHelper.setVisibilityEventListener(this, listener)

fun Activity.isKeyboardVisible() = KeyboardHelper.isKeyboardVisible(this)