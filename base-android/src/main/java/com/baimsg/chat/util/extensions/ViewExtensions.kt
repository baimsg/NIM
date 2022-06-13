package com.baimsg.chat.util.extensions

import android.animation.*
import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import java.util.ArrayList
import java.util.concurrent.atomic.AtomicInteger

object ViewHelper {
    private val sNextGeneratedId = AtomicInteger(1)

}

/**
 * 获取activity的根view
 */
fun Activity.getActivityRoot(): View {
    return (findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup).getChildAt(0)
}

/**
 * 对 View 设置 paddingStart
 *
 * @param value 设置的值
 */
fun View.setPaddingStart(value: Int) {
    if (value != paddingLeft) {
        setPadding(value, paddingTop, paddingRight, paddingBottom)
    }
}

/**
 * 对 View 设置 paddingEnd
 *
 * @param value 设置的值
 */
fun View.setPaddingEnd(value: Int) {
    if (value != paddingRight) {
        setPadding(paddingLeft, paddingTop, value, paddingBottom)
    }
}

/**
 * 对 View 设置 paddingTop
 *
 * @param value 设置的值
 */
fun View.setPaddingTop(value: Int) {
    if (value != paddingTop) {
        setPadding(paddingLeft, value, paddingRight, paddingBottom)
    }
}


/**
 * 对 View 设置 paddingBottom
 *
 * @param value 设置的值
 */
fun View.setPaddingBottom(value: Int) {
    if (value != paddingBottom) {
        setPadding(paddingLeft, paddingTop, paddingRight, value)
    }
}

/**
 * 触发window的insets的广播，使得view的fitSystemWindows得以生效
 */
fun Window.requestApplyInsets() {
    if (Build.VERSION.SDK_INT in 19..20) {
        decorView.requestFitSystemWindows()
    } else if (Build.VERSION.SDK_INT >= 21) {
        decorView.requestApplyInsets()
    }
}

/**
 * 扩展点击区域的范围
 * 需要扩展的元素，此元素必需要有父级元素
 * @param expendSize 需要扩展的尺寸（以sp为单位的）
 */
fun View.expendTouchArea(expendSize: Int) {
    val parentView = parent as View
    parentView.post {
        val rect = Rect()
        getHitRect(rect)
        //如果太早执行本函数，会获取rect失败，因为此时UI界面尚未开始绘制，无法获得正确的坐标
        rect.left -= expendSize
        rect.top -= expendSize
        rect.right += expendSize
        rect.bottom += expendSize
        parentView.touchDelegate = TouchDelegate(rect, this)
    }
}

/**
 * 兼容低版本设置背景
 * @param drawable 背景
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
fun View.setBackgroundCompatible(drawable: Drawable?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        background = drawable
    } else {
        setBackgroundDrawable(drawable)
    }
}

/**
 * 设置背景并填充
 * @param drawable 背景
 */
fun View.setBackgroundKeepingPadding(drawable: Drawable?) {
    val padding =
        intArrayOf(paddingLeft, paddingTop, paddingRight, paddingBottom)
    background = drawable
    setPadding(padding[0], padding[1], padding[2], padding[3])
}

/**
 * 设置背景并填充 直接传 ResId
 * @param backgroundResId 背景颜色
 */
fun View.setBackgroundKeepingPadding(backgroundResId: Int) {
    setBackgroundKeepingPadding(ContextCompat.getDrawable(context, backgroundResId))
}

/**
 * 设置背景颜色
 * @param color 颜色
 */
fun View.setBackgroundColorKeepPadding(@ColorInt color: Int) {
    val padding =
        intArrayOf(paddingLeft, paddingTop, paddingRight, paddingBottom)
    setBackgroundColor(color)
    setPadding(padding[0], padding[1], padding[2], padding[3])
}

/**
 * 对 View 做背景色变化的动作
 *
 * @param bgColor      背景色
 * @param alphaArray   背景色变化的alpha数组，如 int[]{255,0} 表示从纯色变化到透明
 * @param stepDuration 每一步变化的时长
 * @param endAction    动画结束后的回调
 */
fun View.playViewBackgroundAnimation(
    @ColorInt bgColor: Int,
    alphaArray: IntArray = intArrayOf(0, 255, 0),
    stepDuration: Int = 300,
    endAction: Runnable? = null
): Animator {
    val animationCount = alphaArray.size - 1
    val bgDrawable: Drawable = ColorDrawable(bgColor)
    val oldBgDrawable = background
    setBackgroundKeepingPadding(bgDrawable)
    val animatorList: MutableList<Animator> = ArrayList()
    for (i in 0 until animationCount) {
        val animator = ObjectAnimator.ofInt(
            background, "alpha", alphaArray[i],
            alphaArray[i + 1]
        )
        animatorList.add(animator)
    }
    val animatorSet = AnimatorSet()
    animatorSet.duration = stepDuration.toLong()
    animatorSet.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {
            setBackgroundKeepingPadding(oldBgDrawable)
            endAction?.run()
        }

        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    })
    animatorSet.playSequentially(animatorList)
    animatorSet.start()
    return animatorSet
}

/**
 * 对 View 做背景色变化的动作
 *
 * @param startColor   动画开始时 View 的背景色
 * @param endColor     动画结束时 View 的背景色
 * @param duration     动画总时长
 * @param repeatCount  动画重复次数
 * @param setAnimTagId 将动画设置tag给view,若为0则不设置
 * @param endAction    动画结束后的回调
 */
fun View.playViewBackgroundAnimation(
    @ColorInt startColor: Int,
    @ColorInt endColor: Int,
    duration: Long,
    repeatCount: Int = 0,
    setAnimTagId: Int = 0,
    endAction: Runnable? = null
) {
    val oldBgDrawable = background // 存储旧的背景
    setBackgroundColorKeepPadding(startColor)
    val anim = ValueAnimator()
    anim.setIntValues(startColor, endColor)
    anim.duration = duration / (repeatCount + 1)
    anim.repeatCount = repeatCount
    anim.repeatMode = ValueAnimator.REVERSE
    anim.setEvaluator(ArgbEvaluator())
    anim.addUpdateListener { animation ->
        setBackgroundColorKeepPadding(
            animation.animatedValue as Int
        )
    }
    if (setAnimTagId != 0) {
        setTag(setAnimTagId, anim)
    }
    anim.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {
            setBackgroundKeepingPadding(oldBgDrawable)
            endAction?.run()
        }

        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    })
    anim.start()
}

/**
 *
 * 对 View 做透明度变化的进场动画。
 *
 * 相关方法 [fadeOut]
 *
 * @param duration        动画时长(毫秒)
 * @param listener        动画回调
 * @param isNeedAnimation 是否需要动画
 */
fun View.fadeIn(
    duration: Int,
    listener: Animation.AnimationListener?,
    isNeedAnimation: Boolean
): AlphaAnimation? {

    return if (isNeedAnimation) {
        visibility = View.VISIBLE
        val alpha = AlphaAnimation(0F, 1F)
        alpha.interpolator = DecelerateInterpolator()
        alpha.duration = duration.toLong()
        alpha.fillAfter = true
        if (listener != null) {
            alpha.setAnimationListener(listener)
        }
        startAnimation(alpha)
        alpha
    } else {
        alpha = 1f
        visibility = View.VISIBLE
        null
    }
}

/**
 *
 * 对 View 做透明度变化的退场动画
 *
 * 相关方法 [fadeIn]
 *
 * @param duration        动画时长(毫秒)
 * @param listener        动画回调
 * @param isNeedAnimation 是否需要动画
 */
fun View.fadeOut(
    duration: Int,
    listener: Animation.AnimationListener?,
    isNeedAnimation: Boolean
): AlphaAnimation? {

    return if (isNeedAnimation) {
        val alpha = AlphaAnimation(1F, 0F)
        alpha.interpolator = DecelerateInterpolator()
        alpha.duration = duration.toLong()
        alpha.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                listener?.onAnimationStart(animation)
            }

            override fun onAnimationEnd(animation: Animation) {
                visibility = View.GONE
                listener?.onAnimationEnd(animation)
            }

            override fun onAnimationRepeat(animation: Animation) {
                listener?.onAnimationRepeat(animation)
            }
        })
        startAnimation(alpha)
        alpha
    } else {
        visibility = View.GONE
        null
    }
}

/**
 *
 * 对 View 做上下位移的进场动画
 *
 * 相关方法 [slideOut]
 *
 * @param duration        动画时长(毫秒)
 * @param listener        动画回调
 * @param isNeedAnimation 是否需要动画
 * @param direction       进场动画的方向
 * @return 动画对应的 Animator 对象, 注意无动画时返回 null
 */
fun View.slideIn(
    duration: Int,
    listener: Animation.AnimationListener?,
    isNeedAnimation: Boolean,
    direction: Direction
): TranslateAnimation? {
    return if (isNeedAnimation) {
        var translate: TranslateAnimation? = null
        when (direction) {
            Direction.LEFT_TO_RIGHT -> translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
            )
            Direction.TOP_TO_BOTTOM -> translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f
            )
            Direction.RIGHT_TO_LEFT -> translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
            )
            Direction.BOTTOM_TO_TOP -> translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f
            )
        }
        translate.interpolator = DecelerateInterpolator()
        translate.duration = duration.toLong()
        translate.fillAfter = true
        translate.setAnimationListener(listener)
        visibility = View.VISIBLE
        startAnimation(translate)
        translate
    } else {
        clearAnimation()
        visibility = View.VISIBLE
        null
    }
}

/**
 *
 * 对 View 做上下位移的退场动画
 *
 * 相关方法 [slideIn]
 *
 * @param duration        动画时长(毫秒)
 * @param listener        动画回调
 * @param isNeedAnimation 是否需要动画
 * @param direction       进场动画的方向
 * @return 动画对应的 Animator 对象, 注意无动画时返回 null
 */
fun View.slideOut(
    duration: Int,
    listener: Animation.AnimationListener?,
    isNeedAnimation: Boolean,
    direction: Direction
): TranslateAnimation? {
    return if (isNeedAnimation) {
        var translate: TranslateAnimation? = null
        when (direction) {
            Direction.LEFT_TO_RIGHT -> translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
            )
            Direction.TOP_TO_BOTTOM -> translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f
            )
            Direction.RIGHT_TO_LEFT -> translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f
            )
            Direction.BOTTOM_TO_TOP -> translate = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f
            )
        }
        translate.interpolator = DecelerateInterpolator()
        translate.duration = duration.toLong()
        translate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                listener?.onAnimationStart(animation)
            }

            override fun onAnimationEnd(animation: Animation) {
                visibility = View.GONE
                listener?.onAnimationEnd(animation)
            }

            override fun onAnimationRepeat(animation: Animation) {
                listener?.onAnimationRepeat(animation)
            }
        })
        startAnimation(translate)
        translate
    } else {
        clearAnimation()
        visibility = View.GONE
        null
    }
}


/**
 * 清空属性动画
 */
fun Animator.clearValueAnimator() {
    removeAllListeners()
    if (this is ValueAnimator) {
        removeAllUpdateListeners()
    }
    if (Build.VERSION.SDK_INT >= 19) {
        pause()
    }
    cancel()
}

/**
 * 安全的设置图片是否选择
 * @param selected 选择状态
 */
fun ImageView.safeSetImageViewSelected(selected: Boolean) {
    // imageView setSelected 实现有问题。
    // resizeFromDrawable 中判断 drawable size 是否改变而调用 requestLayout，看似合理，但不会被调用
    // 因为 super.setSelected(selected) 会调用 refreshDrawableState
    // 而从 android 6 以后， ImageView 会重载refreshDrawableState，并在里面处理了 drawable size 改变的问题,
    // 从而导致 resizeFromDrawable 的判断失效
    val drawable = drawable ?: return
    val drawableWidth = drawable.intrinsicWidth
    val drawableHeight = drawable.intrinsicHeight
    isSelected = selected
    if (drawable.intrinsicWidth != drawableWidth || drawable.intrinsicHeight != drawableHeight) {
        requestLayout()
    }
}

enum class Direction {
    LEFT_TO_RIGHT, TOP_TO_BOTTOM, RIGHT_TO_LEFT, BOTTOM_TO_TOP
}
