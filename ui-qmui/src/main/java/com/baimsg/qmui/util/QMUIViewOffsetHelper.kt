package com.baimsg.qmui.util

import android.view.View
import androidx.core.view.ViewCompat

/**
 * Create by Baimsg on 2022/6/22
 *
 **/
/**
 * 用于移动 [View] 的实用助手
 * [View.offsetLeftAndRight] and
 * [View.offsetTopAndBottom].
 *
 *
 * 还有绝对偏移的设置（类似于translationX/Y），而不是附加偏移。
 */
class QMUIViewOffsetHelper(private val mView: View) {
    var layoutTop = 0
        private set
    var layoutLeft = 0
        private set
    var topAndBottomOffset = 0
        private set
    var leftAndRightOffset = 0
        private set
    var isVerticalOffsetEnabled = true
    var isHorizontalOffsetEnabled = true

    @JvmOverloads
    fun onViewLayout(applyOffset: Boolean = true) {
        layoutTop = mView.top
        layoutLeft = mView.left
        if (applyOffset) {
            applyOffsets()
        }
    }

    fun applyOffsets() {
        ViewCompat.offsetTopAndBottom(mView, topAndBottomOffset - (mView.top - layoutTop))
        ViewCompat.offsetLeftAndRight(mView, leftAndRightOffset - (mView.left - layoutLeft))
    }

    /**
     * Set the top and bottom offset for this [QMUIViewOffsetHelper]'s view.
     *
     * @param offset the offset in px.
     * @return true if the offset has changed
     */
    fun setTopAndBottomOffset(offset: Int): Boolean {
        if (isVerticalOffsetEnabled && topAndBottomOffset != offset) {
            topAndBottomOffset = offset
            applyOffsets()
            return true
        }
        return false
    }

    /**
     * Set the left and right offset for this [QMUIViewOffsetHelper]'s view.
     *
     * @param offset the offset in px.
     * @return true if the offset has changed
     */
    fun setLeftAndRightOffset(offset: Int): Boolean {
        if (isHorizontalOffsetEnabled && leftAndRightOffset != offset) {
            leftAndRightOffset = offset
            applyOffsets()
            return true
        }
        return false
    }

    fun setOffset(leftOffset: Int, topOffset: Int): Boolean {
        return if (!isHorizontalOffsetEnabled && !isVerticalOffsetEnabled) {
            false
        } else if (isHorizontalOffsetEnabled && isVerticalOffsetEnabled) {
            if (leftAndRightOffset != leftOffset || topAndBottomOffset != topOffset) {
                leftAndRightOffset = leftOffset
                topAndBottomOffset = topOffset
                applyOffsets()
                return true
            }
            false
        } else if (isHorizontalOffsetEnabled) {
            setLeftAndRightOffset(leftOffset)
        } else {
            setTopAndBottomOffset(topOffset)
        }
    }

}