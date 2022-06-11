package com.baimsg.decide.util

import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast
import java.lang.RuntimeException

/**
 *
 * create by baimsg 2021/11/24
 * Email 1469010683@qq.com
 *
 **/
object ToastHelper {
    private const val TAG = "ToastHelper"

    fun show(toast: Toast) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            fixToastForAndroidN(toast).show()
        } else {
            toast.show()
        }
    }

    private fun fixToastForAndroidN(toast: Toast): Toast {
        val tn: Any? = ReflectHelper.getFieldValue(toast, "mTN")
        if (tn == null) {
            Log.w(TAG, "The value of field mTN of $toast is null")
            return toast
        }
        val handler: Any? = ReflectHelper.getFieldValue(tn, "mHandler")
        if (handler is Handler) {
            if (ReflectHelper.setFieldValue(
                    handler, "mCallback", FixCallback(handler)
                )
            ) {
                return toast
            }
        }
        val show: Any? = ReflectHelper.getFieldValue(tn, "mShow")
        if (show is Runnable) {
            if (ReflectHelper.setFieldValue(tn, "mShow", FixRunnable(show))) {
                return toast
            }
        }
        Log.w(TAG, "Neither field mHandler nor mShow of $tn is accessible")
        return toast
    }

    class FixCallback(private val mHandler: Handler) : Handler.Callback {
        override fun handleMessage(msg: Message): Boolean {
            try {
                mHandler.handleMessage(msg)
            } catch (e: Throwable) {
                // ignore
            }
            return true
        }
    }

    class FixRunnable(private val mRunnable: Runnable) : Runnable {
        override fun run() {
            try {
                mRunnable.run()
            } catch (e: RuntimeException) {
                // ignore
            }
        }
    }
}