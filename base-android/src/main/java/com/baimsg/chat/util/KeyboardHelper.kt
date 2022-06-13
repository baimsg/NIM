package com.baimsg.chat.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.baimsg.chat.util.extensions.dp2px
import com.baimsg.chat.util.extensions.getActivityRoot
import kotlin.math.roundToInt

object KeyboardHelper {
    /**
     * 显示软键盘的延迟时间
     */
    private const val SHOW_KEYBOARD_DELAY_TIME = 200
    private const val TAG = "KeyboardHelper"
    const val KEYBOARD_VISIBLE_THRESHOLD_DP = 100f


    fun showKeyboard(editText: EditText, delay: Boolean) {
        showKeyboard(editText, if (delay) SHOW_KEYBOARD_DELAY_TIME else 0)
    }


    /**
     * 针对给定的editText显示软键盘（editText会先获得焦点）. 可以和[hideKeyboard]
     * 搭配使用，进行键盘的显示隐藏控制。
     */
    fun showKeyboard(editText: EditText, delay: Int) {
        if (!editText.requestFocus()) {
            Log.w(TAG, "showSoftInput() can not get focus")
            return
        }
        if (delay > 0) {
            editText.postDelayed({
                val imm = editText.context.applicationContext
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            }, delay.toLong())
        } else {
            val imm = editText.context.applicationContext
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    /**
     * 隐藏软键盘 可以和[showKeyboard]搭配使用，进行键盘的显示隐藏控制。
     *
     * @param view 当前页面上任意一个可用的view
     */
    fun hideKeyboard(view: View): Boolean {
        val inputManager = view.context.applicationContext
            .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // 即使当前焦点不在editText，也是可以隐藏的。
        return inputManager.hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    /**
     * 设置键盘可见性更改事件侦听器.
     *
     * @param activity Activity
     * @param listener KeyboardVisibilityEventListener
     */
    fun setVisibilityEventListener(
        activity: Activity,
        listener: KeyboardVisibilityEventListener
    ) {
        val activityRoot: View = activity.getActivityRoot()
        val layoutListener: OnGlobalLayoutListener = object : OnGlobalLayoutListener {
            private val r = Rect()
            private val visibleThreshold = activity.dp2px(KEYBOARD_VISIBLE_THRESHOLD_DP)
                .roundToInt()
            private var wasOpened = false
            override fun onGlobalLayout() {
                activityRoot.getWindowVisibleDisplayFrame(r)
                val heightDiff = activityRoot.rootView.height - r.height()
                val isOpen = heightDiff > visibleThreshold
                if (isOpen == wasOpened) {
                    // keyboard state has not changed
                    return
                }
                wasOpened = isOpen
                val removeListener = listener.onVisibilityChanged(isOpen, heightDiff)
                if (removeListener) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        activityRoot.viewTreeObserver
                            .removeOnGlobalLayoutListener(this)
                    } else {
                        activityRoot.viewTreeObserver
                            .removeGlobalOnLayoutListener(this)
                    }
                }
            }
        }
        activityRoot.viewTreeObserver.addOnGlobalLayoutListener(layoutListener)
        activity.application
            .registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks(activity) {
                override fun onTargetActivityDestroyed() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        activityRoot.viewTreeObserver
                            .removeOnGlobalLayoutListener(layoutListener)
                    } else {
                        activityRoot.viewTreeObserver
                            .removeGlobalOnLayoutListener(layoutListener)
                    }
                }
            })
    }

    /**
     * 确定键盘是否可见
     *
     * @param activity Activity
     * @return 键盘是否可见
     */
    fun isKeyboardVisible(activity: Activity): Boolean {
        val r = Rect()
        val activityRoot: View = activity.getActivityRoot()
        val visibleThreshold =
            activity.dp2px(KEYBOARD_VISIBLE_THRESHOLD_DP).roundToInt()
        activityRoot.getWindowVisibleDisplayFrame(r)
        val heightDiff = activityRoot.rootView.height - r.height()
        return heightDiff > visibleThreshold
    }


    /**
     * 键盘监听器
     */
    interface KeyboardVisibilityEventListener {
        /**
         * @return 键盘开启关闭状态监听
         */
        fun onVisibilityChanged(isOpen: Boolean, heightDiff: Int): Boolean
    }
}