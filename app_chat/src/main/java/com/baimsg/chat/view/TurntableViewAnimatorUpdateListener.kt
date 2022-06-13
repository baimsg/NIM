package com.baimsg.chat.view

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener


/**
 * Create by Baimsg on 2022/6/10
 *
 **/
class TurntableViewAnimatorUpdateListener(private val turntableView: TurntableView) :
    AnimatorUpdateListener {
    override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
        turntableView.animatedValue = valueAnimator.animatedValue as Float
        turntableView.c()
        turntableView.postInvalidateOnAnimation()
    }
}