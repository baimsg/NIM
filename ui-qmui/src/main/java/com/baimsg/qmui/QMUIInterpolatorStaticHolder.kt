package com.baimsg.qmui

import android.view.animation.*
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator

/**
 * Create by Baimsg on 2022/6/22
 *
 **/
object QMUIInterpolatorStaticHolder {
    val LINEAR_INTERPOLATOR: Interpolator = LinearInterpolator()
    val FAST_OUT_SLOW_IN_INTERPOLATOR: Interpolator = FastOutSlowInInterpolator()
    val FAST_OUT_LINEAR_IN_INTERPOLATOR: Interpolator = FastOutLinearInInterpolator()
    val LINEAR_OUT_SLOW_IN_INTERPOLATOR: Interpolator = LinearOutSlowInInterpolator()
    val DECELERATE_INTERPOLATOR: Interpolator = DecelerateInterpolator()
    val ACCELERATE_INTERPOLATOR: Interpolator = AccelerateInterpolator()
    val ACCELERATE_DECELERATE_INTERPOLATOR: Interpolator = AccelerateDecelerateInterpolator()

    val QUNITIC_INTERPOLATOR =
        Interpolator { t ->
            var t = t
            t -= 1.0f
            t * t * t * t * t + 1.0f
        }
}
