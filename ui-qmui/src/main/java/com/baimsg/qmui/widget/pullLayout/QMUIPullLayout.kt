package com.baimsg.qmui.widget.pullLayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.OverScroller
import androidx.annotation.IntDef
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.baimsg.qmui.QMUIInterpolatorStaticHolder.QUNITIC_INTERPOLATOR
import com.baimsg.qmui.R
import com.baimsg.qmui.util.QMUIViewOffsetHelper

class QMUIPullLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.QMUIPullLayoutStyle
) :
    FrameLayout(context, attrs, defStyleAttr), NestedScrollingParent3 {

    companion object {
        const val DEFAULT_PULL_RATE = 0.45f
        const val DEFAULT_FLING_FRACTION = 0.002f
        const val DEFAULT_SCROLL_SPEED_PER_PIXEL = 1.5f
        const val DEFAULT_MIN_SCROLL_DURATION = 300
        const val PULL_EDGE_LEFT = 0x01
        const val PULL_EDGE_TOP = 0x02
        const val PULL_EDGE_RIGHT = 0x04
        const val PULL_EDGE_BOTTOM = 0x08
        const val PUL_EDGE_ALL =
            PULL_EDGE_LEFT or PULL_EDGE_TOP or PULL_EDGE_RIGHT or PULL_EDGE_BOTTOM
        private const val STATE_IDLE = 0
        private const val STATE_PULLING = 1
        private const val STATE_SETTLING_TO_TRIGGER_OFFSET = 2
        private const val STATE_TRIGGERING = 3
        private const val STATE_SETTLING_TO_INIT_OFFSET = 4
        private const val STATE_SETTLING_DELIVER = 5
        private const val STATE_SETTLING_FLING = 6
    }


    @IntDef(PULL_EDGE_LEFT, PULL_EDGE_TOP, PULL_EDGE_RIGHT, PULL_EDGE_BOTTOM)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class PullEdge

    private var mEnabledEdges: Int
    private var mTargetView: View? = null
    private var mTargetOffsetHelper: QMUIViewOffsetHelper? = null
    private var mLeftPullAction: PullAction? = null
    private var mTopPullAction: PullAction? = null
    private var mRightPullAction: PullAction? = null
    private var mBottomPullAction: PullAction? = null
    private var mActionListener: ActionListener? = null

    // 用于从 v2 版本的 onNestedScroll 调用到 v3 版本的 onNestedScroll 的数组。
    //  这只是为了防止在 API 21 之前存在的 GC 和对象实例化成本。
    private val mNestedScrollingV2ConsumedCompat = IntArray(2)

    private var mStopTargetViewFlingImpl: StopTargetViewFlingImpl? =
        DefaultStopTargetViewFlingImpl.instance
    private var mStopTargetFlingRunnable: Runnable? = null
    private val mScroller: OverScroller
    private var mNestedPreFlingVelocityScaleDown = 10f
    private var mMinScrollDuration = DEFAULT_MIN_SCROLL_DURATION
    private var mState = STATE_IDLE
    private val mNestedScrollingParentHelper: NestedScrollingParentHelper

    init {
        val array = context.obtainStyledAttributes(
            attrs,
            R.styleable.QMUIPullLayout, defStyleAttr, 0
        )
        mEnabledEdges = array.getInt(R.styleable.QMUIPullLayout_qmui_pull_enable_edge, PUL_EDGE_ALL)
        array.recycle()
        mNestedScrollingParentHelper = NestedScrollingParentHelper(this)
        mScroller = OverScroller(context, QUNITIC_INTERPOLATOR)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        var isTargetSet = false
        var edgesSet = 0
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            val lp = view.layoutParams as LayoutParams
            if (lp.isTarget) {
                if (isTargetSet) {
                    throw RuntimeException(
                        "More than one view in xml are marked by qmui_is_target = true."
                    )
                }
                isTargetSet = true
                setTargetView(view)
            } else {
                if (edgesSet and lp.edge != 0) {
                    var text = ""
                    when (lp.edge) {
                        PULL_EDGE_LEFT -> {
                            text = "left"
                        }
                        PULL_EDGE_TOP -> {
                            text = "top"
                        }
                        PULL_EDGE_RIGHT -> {
                            text = "right"
                        }
                        PULL_EDGE_BOTTOM -> {
                            text = "bottom"
                        }
                    }
                    throw RuntimeException("More than one view in xml marked by qmui_layout_edge = $text")
                }
                edgesSet = edgesSet or lp.edge
                setActionView(view, lp)
            }
        }
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            if (mScroller.isFinished) {
                if (mState == STATE_SETTLING_TO_INIT_OFFSET) {
                    mState = STATE_IDLE
                    return
                }
                if (mState == STATE_TRIGGERING) {
                    return
                }
                if (mState == STATE_SETTLING_FLING) {
                    checkScrollToTargetOffsetOrInitOffset(false)
                    return
                }
                if (mState == STATE_SETTLING_TO_TRIGGER_OFFSET) {
                    mState = STATE_TRIGGERING
                    if (mLeftPullAction != null && isEdgeEnabled(PULL_EDGE_LEFT)) {
                        if (mScroller.finalX == mLeftPullAction!!.targetTriggerOffset) {
                            onActionTriggered(mLeftPullAction!!)
                        }
                    }
                    if (mRightPullAction != null && isEdgeEnabled(PULL_EDGE_RIGHT)) {
                        if (mScroller.finalX == -mRightPullAction!!.targetTriggerOffset) {
                            onActionTriggered(mRightPullAction!!)
                        }
                    }
                    if (mTopPullAction != null && isEdgeEnabled(PULL_EDGE_TOP)) {
                        if (mScroller.finalY == mTopPullAction!!.targetTriggerOffset) {
                            onActionTriggered(mTopPullAction!!)
                        }
                    }
                    if (mBottomPullAction != null && isEdgeEnabled(PULL_EDGE_BOTTOM)) {
                        if (mScroller.finalY == -mBottomPullAction!!.targetTriggerOffset) {
                            onActionTriggered(mBottomPullAction!!)
                        }
                    }
                    setHorOffsetToTargetOffsetHelper(mScroller.currX)
                    setVerOffsetToTargetOffsetHelper(mScroller.currY)
                }
            } else {
                setHorOffsetToTargetOffsetHelper(mScroller.currX)
                setVerOffsetToTargetOffsetHelper(mScroller.currY)
                postInvalidateOnAnimation()
            }
        }
    }

    fun setStopTargetViewFlingImpl(stopTargetViewFlingImpl: StopTargetViewFlingImpl) {
        mStopTargetViewFlingImpl = stopTargetViewFlingImpl
    }

    fun setMinScrollDuration(minScrollDuration: Int) {
        mMinScrollDuration = minScrollDuration
    }

    fun setTargetView(view: View) {
        if (view.parent !== this) {
            throw RuntimeException("Target already exists other parent view.")
        }
        if (view.parent == null) {
            val lp = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            addView(view, lp)
        }
        innerSetTargetView(view)
    }

    private fun innerSetTargetView(view: View) {
        mTargetView = view
        mTargetOffsetHelper = QMUIViewOffsetHelper(view)
    }

    fun setActionView(view: View, lp: LayoutParams) {
        val builder = PullActionBuilder(view, lp.edge)
            .canOverPull(lp.canOverPull)
            .pullRate(lp.pullRate)
            .needReceiveFlingFromTargetView(lp.needReceiveFlingFromTarget)
            .receivedFlingFraction(lp.receivedFlingFraction)
            .scrollSpeedPerPixel(lp.scrollSpeedPerPixel)
            .targetTriggerOffset(lp.targetTriggerOffset)
            .triggerUntilScrollToTriggerOffset(lp.triggerUntilScrollToTriggerOffset)
            .scrollToTriggerOffsetAfterTouchUp(lp.scrollToTriggerOffsetAfterTouchUp)
            .actionInitOffset(lp.actionInitOffset)
        view.layoutParams = lp
        setActionView(builder)
    }

    fun setActionView(builder: PullActionBuilder) {
        if (builder.mActionView.parent !== this) {
            throw RuntimeException("Action view already exists other parent view.")
        }
        if (builder.mActionView.parent == null) {
            var lp = builder.mActionView.layoutParams
            if (lp == null) {
                lp = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            addView(builder.mActionView, lp)
        }
        if (builder.mPullEdge == PULL_EDGE_LEFT) {
            mLeftPullAction = builder.build()
        } else if (builder.mPullEdge == PULL_EDGE_TOP) {
            mTopPullAction = builder.build()
        } else if (builder.mPullEdge == PULL_EDGE_RIGHT) {
            mRightPullAction = builder.build()
        } else if (builder.mPullEdge == PULL_EDGE_BOTTOM) {
            mBottomPullAction = builder.build()
        }
    }

    fun setActionListener(actionListener: ActionListener?) {
        mActionListener = actionListener
    }

    fun setEnabledEdges(enabledEdges: Int) {
        mEnabledEdges = enabledEdges
    }

    fun isEdgeEnabled(@PullEdge edge: Int): Boolean {
        return mEnabledEdges and edge == edge && getPullAction(edge) != null
    }

    private fun getPullAction(@PullEdge edge: Int): PullAction? {
        if (edge == PULL_EDGE_LEFT) {
            return mLeftPullAction
        } else if (edge == PULL_EDGE_TOP) {
            return mTopPullAction
        } else if (edge == PULL_EDGE_RIGHT) {
            return mRightPullAction
        } else if (edge == PULL_EDGE_BOTTOM) {
            return mBottomPullAction
        }
        return null
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val w = r - l
        val h = b - t
        if (mTargetView != null) {
            mTargetView!!.layout(0, 0, w, h)
            mTargetOffsetHelper?.onViewLayout()
        }
        if (mLeftPullAction != null) {
            val view = mLeftPullAction!!.mActionView
            val vw = view.measuredWidth
            val vh = view.measuredHeight
            val vc = (h - vh) / 2
            view.layout(-vw, vc, 0, vc + vh)
            mLeftPullAction!!.mViewOffsetHelper.onViewLayout()
        }
        if (mTopPullAction != null) {
            val view = mTopPullAction!!.mActionView
            val vw = view.measuredWidth
            val vh = view.measuredHeight
            val vc = (w - vw) / 2
            view.layout(vc, -vh, vc + vw, 0)
            mTopPullAction!!.mViewOffsetHelper.onViewLayout()
        }
        if (mRightPullAction != null) {
            val view = mRightPullAction!!.mActionView
            val vw = view.measuredWidth
            val vh = view.measuredHeight
            val vc = (h - vh) / 2
            view.layout(w, vc, w + vw, vc + vh)
            mRightPullAction!!.mViewOffsetHelper.onViewLayout()
        }
        if (mBottomPullAction != null) {
            val view = mBottomPullAction!!.mActionView
            val vw = view.measuredWidth
            val vh = view.measuredHeight
            val vc = (w - vw) / 2
            view.layout(vc, h, vc + vw, h + vh)
            mBottomPullAction!!.mViewOffsetHelper.onViewLayout()
        }
    }

    fun setNestedPreFlingVelocityScaleDown(nestedPreFlingVelocityScaleDown: Float) {
        mNestedPreFlingVelocityScaleDown = nestedPreFlingVelocityScaleDown
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return mTargetView === target && axes == ViewCompat.SCROLL_AXIS_HORIZONTAL && (isEdgeEnabled(
            PULL_EDGE_LEFT
        ) || isEdgeEnabled(PULL_EDGE_RIGHT)) ||
                axes == ViewCompat.SCROLL_AXIS_VERTICAL && (isEdgeEnabled(
            PULL_EDGE_TOP
        ) || isEdgeEnabled(PULL_EDGE_BOTTOM))
    }

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return onStartNestedScroll(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        if (type == ViewCompat.TYPE_TOUCH) {
            removeStopTargetFlingRunnable()
            mScroller.abortAnimation()
            mState = STATE_PULLING
        }
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes)
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        onNestedScrollAccepted(child, target, axes, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        var dx = dx
        var dy = dy
        val originDx = dx
        val originDy = dy
        dy = checkEdgeTopScrollDown(dy, consumed, type)
        dy = checkEdgeBottomScrollDown(dy, consumed, type)
        dy = checkEdgeTopScrollUp(dy, consumed, type)
        dy = checkEdgeBottomScrollUp(dy, consumed, type)
        dx = checkEdgeLeftScrollRight(dx, consumed, type)
        dx = checkEdgeRightScrollRight(dx, consumed, type)
        dx = checkEdgeLeftScrollLeft(dx, consumed, type)
        dx = checkEdgeRightScrollLeft(dx, consumed, type)
        if (originDx == dx && originDy == dy && mState == STATE_SETTLING_DELIVER) {
            checkStopTargetFling(target, dx, dy, type)
        }
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScroll(
        target: View, dxConsumed: Int, dyConsumed: Int,
        dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray
    ) {
        var dxUnconsumed = dxUnconsumed
        var dyUnconsumed = dyUnconsumed
        val originDxUnconsumed = dxUnconsumed
        val originDyUnconsumed = dyUnconsumed
        dyUnconsumed = checkEdgeTopScrollDown(dyUnconsumed, consumed, type)
        dyUnconsumed = checkEdgeBottomScrollDown(dyUnconsumed, consumed, type)
        dyUnconsumed = checkEdgeTopScrollUp(dyUnconsumed, consumed, type)
        dyUnconsumed = checkEdgeBottomScrollUp(dyUnconsumed, consumed, type)
        dxUnconsumed = checkEdgeLeftScrollRight(dxUnconsumed, consumed, type)
        dxUnconsumed = checkEdgeRightScrollRight(dxUnconsumed, consumed, type)
        dxUnconsumed = checkEdgeLeftScrollLeft(dxUnconsumed, consumed, type)
        dxUnconsumed = checkEdgeRightScrollLeft(dxUnconsumed, consumed, type)
        if (dyUnconsumed == originDyUnconsumed && dxUnconsumed == originDxUnconsumed && mState == STATE_SETTLING_DELIVER) {
            checkStopTargetFling(target, dxUnconsumed, dyUnconsumed, type)
        }
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        onNestedScroll(
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            mNestedScrollingV2ConsumedCompat
        )
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        onNestedScroll(
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            ViewCompat.TYPE_TOUCH
        )
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        var velocityX = velocityX
        var velocityY = velocityY
        val hOffset: Int = mTargetOffsetHelper!!.leftAndRightOffset
        val vOffset: Int = mTargetOffsetHelper!!.topAndBottomOffset

        // if the targetView is RecyclerView and we set OnFlingListener for RecyclerView.
        // then the targetView can not deliver fling consume to NestedScrollParent
        // so we intercept the fling if the target view can not consume the fling.
        if (mLeftPullAction != null && isEdgeEnabled(PULL_EDGE_LEFT)) {
            if (velocityX < 0 && !mTargetView!!.canScrollHorizontally(-1)) {
                mState = STATE_SETTLING_FLING
                velocityX /= mNestedPreFlingVelocityScaleDown
                val maxX =
                    if (mLeftPullAction!!.isCanOverPull) Int.MAX_VALUE else mLeftPullAction!!.targetTriggerOffset
                mScroller.fling(hOffset, vOffset, -velocityX.toInt(), 0, 0, maxX, vOffset, vOffset)
                postInvalidateOnAnimation()
                return true
            } else if (velocityX > 0 && hOffset > 0) {
                mState = STATE_SETTLING_TO_INIT_OFFSET
                mScroller.startScroll(
                    hOffset,
                    vOffset,
                    -hOffset,
                    0,
                    scrollDuration(mLeftPullAction!!, hOffset)
                )
                postInvalidateOnAnimation()
                return true
            }
        }
        if (mRightPullAction != null && isEdgeEnabled(PULL_EDGE_RIGHT)) {
            if (velocityX > 0 && !mTargetView!!.canScrollHorizontally(1)) {
                mState = STATE_SETTLING_FLING
                velocityX /= mNestedPreFlingVelocityScaleDown
                val minX =
                    if (mRightPullAction!!.isCanOverPull) Int.MIN_VALUE else -mRightPullAction!!.targetTriggerOffset
                mScroller.fling(hOffset, vOffset, -velocityX.toInt(), 0, minX, 0, vOffset, vOffset)
                postInvalidateOnAnimation()
                return true
            } else if (velocityX < 0 && hOffset < 0) {
                mState = STATE_SETTLING_TO_INIT_OFFSET
                mScroller.startScroll(
                    hOffset,
                    vOffset,
                    -hOffset,
                    0,
                    scrollDuration(mRightPullAction!!, hOffset)
                )
                postInvalidateOnAnimation()
                return true
            }
        }
        if (mTopPullAction != null && isEdgeEnabled(PULL_EDGE_TOP)) {
            if (velocityY < 0 && !mTargetView!!.canScrollVertically(-1)) {
                mState = STATE_SETTLING_FLING
                velocityY /= mNestedPreFlingVelocityScaleDown
                val maxY =
                    if (mTopPullAction!!.isCanOverPull) Int.MAX_VALUE else mTopPullAction!!.targetTriggerOffset
                mScroller.fling(hOffset, vOffset, 0, -velocityY.toInt(), hOffset, hOffset, 0, maxY)
                postInvalidateOnAnimation()
                return true
            } else if (velocityY > 0 && vOffset > 0) {
                mState = STATE_SETTLING_TO_INIT_OFFSET
                mScroller.startScroll(
                    hOffset,
                    vOffset,
                    0,
                    -vOffset,
                    scrollDuration(mTopPullAction!!, vOffset)
                )
                postInvalidateOnAnimation()
                return true
            }
        }
        if (mBottomPullAction != null && isEdgeEnabled(PULL_EDGE_BOTTOM)) {
            if (velocityY > 0 && !mTargetView!!.canScrollVertically(1)) {
                mState = STATE_SETTLING_FLING
                velocityY /= mNestedPreFlingVelocityScaleDown
                val minY =
                    if (mBottomPullAction!!.isCanOverPull) Int.MIN_VALUE else -mBottomPullAction!!.targetTriggerOffset
                mScroller.fling(hOffset, vOffset, 0, -velocityY.toInt(), hOffset, hOffset, minY, 0)
                postInvalidateOnAnimation()
                return true
            } else if (velocityY < 0 && vOffset < 0) {
                mState = STATE_SETTLING_TO_INIT_OFFSET
                mScroller.startScroll(
                    hOffset, vOffset, 0, -vOffset, scrollDuration(
                        mBottomPullAction!!, vOffset
                    )
                )
                postInvalidateOnAnimation()
                return true
            }
        }
        mState = STATE_SETTLING_DELIVER
        return super.onNestedPreFling(target, velocityX, velocityY)
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        if (mState == STATE_PULLING) {
            checkScrollToTargetOffsetOrInitOffset(false)
        } else if (mState == STATE_SETTLING_DELIVER && type != ViewCompat.TYPE_TOUCH) {
            removeStopTargetFlingRunnable()
            checkScrollToTargetOffsetOrInitOffset(false)
        }
    }

    private fun scrollDuration(pullAction: PullAction, delta: Int): Int {
        return Math.max(
            mMinScrollDuration,
            Math.abs((pullAction.scrollSpeedPerPixel * delta).toInt())
        )
    }

    private fun onActionTriggered(pullAction: PullAction) {
        if (pullAction.mIsActionRunning) {
            return
        }
        pullAction.mIsActionRunning = true
        if (mActionListener != null) {
            mActionListener!!.onActionTriggered(pullAction)
        }
        if (pullAction.mActionView is ActionPullWatcherView) {
            (pullAction.mActionView as ActionPullWatcherView).onActionTriggered()
        }
    }

    @JvmOverloads
    fun finishActionRun(pullAction: PullAction, animate: Boolean = true) {
        if (pullAction != getPullAction(pullAction.pullEdge)) {
            return
        }
        pullAction.mIsActionRunning = false
        if (pullAction.mActionView is ActionPullWatcherView) {
            (pullAction.mActionView as ActionPullWatcherView).onActionFinished()
        }
        if (mState == STATE_PULLING) {
            return
        }
        if (!animate) {
            mState = STATE_IDLE
            setVerOffsetToTargetOffsetHelper(0)
            setHorOffsetToTargetOffsetHelper(0)
            return
        }
        mState = STATE_SETTLING_TO_INIT_OFFSET
        @PullEdge val pullEdge = pullAction.pullEdge
        val vOffset: Int = mTargetOffsetHelper!!.topAndBottomOffset
        val hOffset: Int = mTargetOffsetHelper!!.leftAndRightOffset
        if (pullEdge == PULL_EDGE_TOP && mTopPullAction != null && vOffset > 0) {
            mScroller.startScroll(
                hOffset,
                vOffset,
                0,
                -vOffset,
                scrollDuration(mTopPullAction!!, vOffset)
            )
            postInvalidateOnAnimation()
        } else if (pullEdge == PULL_EDGE_BOTTOM && mBottomPullAction != null && vOffset < 0) {
            mScroller.startScroll(
                hOffset,
                vOffset,
                0,
                -vOffset,
                scrollDuration(mBottomPullAction!!, vOffset)
            )
            postInvalidateOnAnimation()
        } else if (pullEdge == PULL_EDGE_LEFT && mLeftPullAction != null && hOffset > 0) {
            mScroller.startScroll(
                hOffset,
                vOffset,
                -hOffset,
                0,
                scrollDuration(mLeftPullAction!!, hOffset)
            )
            postInvalidateOnAnimation()
        } else if (pullEdge == PULL_EDGE_RIGHT && mRightPullAction != null && hOffset < 0) {
            mScroller.startScroll(
                hOffset,
                vOffset,
                -hOffset,
                0,
                scrollDuration(mRightPullAction!!, hOffset)
            )
            postInvalidateOnAnimation()
        }
    }

    private fun checkScrollToTargetOffsetOrInitOffset(forceInit: Boolean) {
        if (mTargetView == null) {
            return
        }
        mScroller.abortAnimation()
        val hOffset: Int = mTargetOffsetHelper!!.leftAndRightOffset
        val vOffset: Int = mTargetOffsetHelper!!.topAndBottomOffset
        var hTarget = 0
        var vTarget = 0
        if (mLeftPullAction != null && isEdgeEnabled(PULL_EDGE_LEFT) && hOffset > 0) {
            mState = STATE_SETTLING_TO_INIT_OFFSET
            if (!forceInit) {
                val targetOffset = mLeftPullAction!!.targetTriggerOffset
                if (hOffset == targetOffset) {
                    onActionTriggered(mLeftPullAction!!)
                    return
                }
                if (hOffset > targetOffset) {
                    if (!mLeftPullAction!!.isScrollToTriggerOffsetAfterTouchUp) {
                        mState = STATE_TRIGGERING
                        onActionTriggered(mLeftPullAction!!)
                        return
                    }
                    if (!mLeftPullAction!!.isTriggerUntilScrollToTriggerOffset) {
                        mState = STATE_TRIGGERING
                        onActionTriggered(mLeftPullAction!!)
                    } else {
                        mState = STATE_SETTLING_TO_TRIGGER_OFFSET
                    }
                    hTarget = targetOffset
                }
            }
            val dx = hTarget - hOffset
            mScroller.startScroll(hOffset, vOffset, dx, 0, scrollDuration(mLeftPullAction!!, dx))
            postInvalidateOnAnimation()
            return
        }
        if (mRightPullAction != null && isEdgeEnabled(PULL_EDGE_RIGHT) && hOffset < 0) {
            mState = STATE_SETTLING_TO_INIT_OFFSET
            if (!forceInit) {
                val targetOffset = mRightPullAction!!.targetTriggerOffset
                if (hOffset == -targetOffset) {
                    mState = STATE_TRIGGERING
                    onActionTriggered(mRightPullAction!!)
                    return
                }
                if (hOffset < -targetOffset) {
                    if (!mRightPullAction!!.isScrollToTriggerOffsetAfterTouchUp) {
                        mState = STATE_TRIGGERING
                        onActionTriggered(mRightPullAction!!)
                        return
                    }
                    if (!mRightPullAction!!.isTriggerUntilScrollToTriggerOffset) {
                        mState = STATE_TRIGGERING
                        onActionTriggered(mRightPullAction!!)
                    } else {
                        mState = STATE_SETTLING_TO_TRIGGER_OFFSET
                    }
                    hTarget = -targetOffset
                }
            }
            val dx = hTarget - hOffset
            mScroller.startScroll(hOffset, vOffset, dx, 0, scrollDuration(mRightPullAction!!, dx))
            postInvalidateOnAnimation()
            return
        }
        if (mTopPullAction != null && isEdgeEnabled(PULL_EDGE_TOP) && vOffset > 0) {
            mState = STATE_SETTLING_TO_INIT_OFFSET
            if (!forceInit) {
                val targetOffset = mTopPullAction!!.targetTriggerOffset
                if (vOffset == targetOffset) {
                    mState = STATE_TRIGGERING
                    onActionTriggered(mTopPullAction!!)
                    return
                }
                if (vOffset > targetOffset) {
                    if (!mTopPullAction!!.isScrollToTriggerOffsetAfterTouchUp) {
                        mState = STATE_TRIGGERING
                        onActionTriggered(mTopPullAction!!)
                        return
                    }
                    if (!mTopPullAction!!.isTriggerUntilScrollToTriggerOffset) {
                        mState = STATE_TRIGGERING
                        onActionTriggered(mTopPullAction!!)
                    } else {
                        mState = STATE_SETTLING_TO_TRIGGER_OFFSET
                    }
                    vTarget = targetOffset
                }
            }
            val dy = vTarget - vOffset
            mScroller.startScroll(
                hOffset,
                vOffset,
                hOffset,
                dy,
                scrollDuration(mTopPullAction!!, dy)
            )
            postInvalidateOnAnimation()
            return
        }
        if (mBottomPullAction != null && isEdgeEnabled(PULL_EDGE_BOTTOM) && vOffset < 0) {
            mState = STATE_SETTLING_TO_INIT_OFFSET
            if (!forceInit) {
                val targetOffset = mBottomPullAction!!.targetTriggerOffset
                if (vOffset == -targetOffset) {
                    onActionTriggered(mBottomPullAction!!)
                    return
                }
                if (vOffset < -targetOffset) {
                    if (!mBottomPullAction!!.isScrollToTriggerOffsetAfterTouchUp) {
                        mState = STATE_TRIGGERING
                        onActionTriggered(mBottomPullAction!!)
                        return
                    }
                    if (!mBottomPullAction!!.isTriggerUntilScrollToTriggerOffset) {
                        mState = STATE_TRIGGERING
                        onActionTriggered(mBottomPullAction!!)
                    } else {
                        mState = STATE_SETTLING_TO_TRIGGER_OFFSET
                    }
                    vTarget = -targetOffset
                }
            }
            val dy = vTarget - vOffset
            mScroller.startScroll(
                hOffset,
                vOffset,
                hOffset,
                dy,
                scrollDuration(mBottomPullAction!!, dy)
            )
            postInvalidateOnAnimation()
            return
        }
        mState = STATE_IDLE
    }

    private fun removeStopTargetFlingRunnable() {
        if (mStopTargetFlingRunnable != null) {
            removeCallbacks(mStopTargetFlingRunnable)
            mStopTargetFlingRunnable = null
        }
    }

    private fun checkStopTargetFling(targetView: View, dx: Int, dy: Int, type: Int) {
        if (mStopTargetFlingRunnable != null || type == ViewCompat.TYPE_TOUCH) {
            return
        }
        if (dy < 0 && !mTargetView!!.canScrollVertically(-1) ||
            dy > 0 && !mTargetView!!.canScrollVertically(1) ||
            dx < 0 && !mTargetView!!.canScrollHorizontally(-1) ||
            dx > 0 && !mTargetView!!.canScrollHorizontally(1)
        ) {
            mStopTargetFlingRunnable = Runnable {
                mStopTargetViewFlingImpl!!.stopFling(targetView)
                mStopTargetFlingRunnable = null
                checkScrollToTargetOffsetOrInitOffset(false)
            }
            post(mStopTargetFlingRunnable)
        }
    }

    private fun setHorOffsetToTargetOffsetHelper(hOffset: Int) {
        mTargetOffsetHelper!!.setLeftAndRightOffset(hOffset)
        onTargetViewLeftAndRightOffsetChanged(hOffset)
        if (mLeftPullAction != null) {
            mLeftPullAction!!.onTargetMoved(hOffset)
            if (mLeftPullAction!!.mActionView is ActionPullWatcherView) {
                (mLeftPullAction!!.mActionView as ActionPullWatcherView).onPull(
                    mLeftPullAction,
                    hOffset
                )
            }
        }
        if (mRightPullAction != null) {
            mRightPullAction!!.onTargetMoved(-hOffset)
            if (mRightPullAction!!.mActionView is ActionPullWatcherView) {
                (mRightPullAction!!.mActionView as ActionPullWatcherView).onPull(
                    mRightPullAction,
                    -hOffset
                )
            }
        }
    }

    private fun setVerOffsetToTargetOffsetHelper(vOffset: Int) {
        mTargetOffsetHelper!!.setTopAndBottomOffset(vOffset)
        onTargetViewTopAndBottomOffsetChanged(vOffset)
        if (mTopPullAction != null) {
            mTopPullAction!!.onTargetMoved(vOffset)
            if (mTopPullAction!!.mActionView is ActionPullWatcherView) {
                (mTopPullAction!!.mActionView as ActionPullWatcherView).onPull(
                    mTopPullAction,
                    vOffset
                )
            }
        }
        if (mBottomPullAction != null) {
            mBottomPullAction!!.onTargetMoved(-vOffset)
            if (mBottomPullAction!!.mActionView is ActionPullWatcherView) {
                (mBottomPullAction!!.mActionView as ActionPullWatcherView).onPull(
                    mBottomPullAction,
                    -vOffset
                )
            }
        }
    }

    protected fun onTargetViewTopAndBottomOffsetChanged(vOffset: Int) {}
    protected fun onTargetViewLeftAndRightOffsetChanged(hOffset: Int) {}
    private fun checkEdgeTopScrollDown(dy: Int, consumed: IntArray, type: Int): Int {
        var dy = dy
        var vOffset: Int = mTargetOffsetHelper!!.topAndBottomOffset
        if (dy > 0 && isEdgeEnabled(PULL_EDGE_TOP) && vOffset > 0) {
            val pullRate = if (type == ViewCompat.TYPE_TOUCH) mTopPullAction!!.pullRate else 1f
            val ry = (dy * pullRate).toInt()
            if (ry == 0) {
                return dy
            }
            if (vOffset >= ry) {
                consumed[1] += dy
                vOffset -= ry
                dy = 0
            } else {
                val yConsumed = (vOffset / pullRate).toInt()
                consumed[1] += yConsumed
                dy -= yConsumed
                vOffset = 0
            }
            setVerOffsetToTargetOffsetHelper(vOffset)
        }
        return dy
    }

    private fun checkEdgeTopScrollUp(dy: Int, consumed: IntArray, type: Int): Int {
        var dy = dy
        if (dy < 0 && isEdgeEnabled(PULL_EDGE_TOP) && !mTargetView!!.canScrollVertically(-1) &&
            (type == ViewCompat.TYPE_TOUCH || mTopPullAction!!.isNeedReceiveFlingFromTargetView)
        ) {
            var vOffset: Int = mTargetOffsetHelper!!.topAndBottomOffset
            val pullRate =
                if (type == ViewCompat.TYPE_TOUCH) mTopPullAction!!.pullRate else mTopPullAction!!.getFlingRate(
                    vOffset
                )
            val ry = (dy * pullRate).toInt()
            if (ry == 0) {
                return dy
            }
            if (mTopPullAction!!.isCanOverPull || -ry <= mTopPullAction!!.targetTriggerOffset - vOffset) {
                vOffset -= ry
                consumed[1] += dy
                dy = 0
            } else {
                val yConsumed =
                    ((vOffset - mTopPullAction!!.targetTriggerOffset) / pullRate).toInt()
                consumed[1] += yConsumed
                dy -= yConsumed
                vOffset = mBottomPullAction!!.targetTriggerOffset
            }
            setVerOffsetToTargetOffsetHelper(vOffset)
        }
        return dy
    }

    private fun checkEdgeBottomScrollDown(dy: Int, consumed: IntArray, type: Int): Int {
        var dy = dy
        if (dy > 0 && isEdgeEnabled(PULL_EDGE_BOTTOM) && !mTargetView!!.canScrollVertically(1) &&
            (type == ViewCompat.TYPE_TOUCH || mBottomPullAction!!.isNeedReceiveFlingFromTargetView)
        ) {
            var vOffset: Int = mTargetOffsetHelper!!.topAndBottomOffset
            val pullRate =
                if (type == ViewCompat.TYPE_TOUCH) mBottomPullAction!!.pullRate else mBottomPullAction!!.getFlingRate(
                    -vOffset
                )
            val ry = (dy * pullRate).toInt()
            if (ry == 0) {
                return dy
            }
            if (mBottomPullAction!!.isCanOverPull || vOffset - ry >= -mBottomPullAction!!.targetTriggerOffset) {
                vOffset -= ry
                consumed[1] += dy
                dy = 0
            } else {
                val yConsumed =
                    ((-mBottomPullAction!!.targetTriggerOffset - vOffset) / pullRate).toInt()
                consumed[1] += yConsumed
                dy -= yConsumed
                vOffset = -mBottomPullAction!!.targetTriggerOffset
            }
            setVerOffsetToTargetOffsetHelper(vOffset)
        }
        return dy
    }

    private fun checkEdgeBottomScrollUp(dy: Int, consumed: IntArray, type: Int): Int {
        var dy = dy
        var vOffset: Int = mTargetOffsetHelper!!.topAndBottomOffset
        if (dy < 0 && isEdgeEnabled(PULL_EDGE_BOTTOM) && vOffset < 0) {
            val pullRate = if (type == ViewCompat.TYPE_TOUCH) mBottomPullAction!!.pullRate else 1f
            val ry = (dy * pullRate).toInt()
            if (ry == 0) {
                return dy
            }
            if (vOffset <= ry) {
                consumed[1] += dy
                vOffset -= ry
                dy = 0
            } else {
                val yConsumed = (vOffset / pullRate).toInt()
                consumed[1] += yConsumed
                dy -= yConsumed
                vOffset = 0
            }
            setVerOffsetToTargetOffsetHelper(vOffset)
        }
        return dy
    }

    private fun checkEdgeLeftScrollRight(dx: Int, consumed: IntArray, type: Int): Int {
        var dx = dx
        var hOffset: Int = mTargetOffsetHelper!!.leftAndRightOffset
        if (dx > 0 && isEdgeEnabled(PULL_EDGE_LEFT) && hOffset > 0) {
            val pullRate = if (type == ViewCompat.TYPE_TOUCH) mLeftPullAction!!.pullRate else 1f
            val rx = (dx * pullRate).toInt()
            if (rx == 0) {
                return dx
            }
            if (hOffset >= rx) {
                consumed[0] += dx
                hOffset -= rx
                dx = 0
            } else {
                val xConsumed = (hOffset / pullRate).toInt()
                consumed[0] += xConsumed
                dx -= xConsumed
                hOffset = 0
            }
            setHorOffsetToTargetOffsetHelper(hOffset)
        }
        return dx
    }

    private fun checkEdgeLeftScrollLeft(dx: Int, consumed: IntArray, type: Int): Int {
        var dx = dx
        var hOffset: Int = mTargetOffsetHelper!!.leftAndRightOffset
        if (dx < 0 && isEdgeEnabled(PULL_EDGE_LEFT) && !mTargetView!!.canScrollHorizontally(-1) &&
            (type == ViewCompat.TYPE_TOUCH || mLeftPullAction!!.isNeedReceiveFlingFromTargetView)
        ) {
            val pullRate =
                if (type == ViewCompat.TYPE_TOUCH) mLeftPullAction!!.pullRate else mLeftPullAction!!.getFlingRate(
                    hOffset
                )
            val rx = (dx * pullRate).toInt()
            if (rx == 0) {
                return dx
            }
            if (mLeftPullAction!!.isCanOverPull || -rx <= mLeftPullAction!!.targetTriggerOffset - hOffset) {
                hOffset -= rx
                consumed[0] += dx
                dx = 0
            } else {
                val xConsumed =
                    ((hOffset - mLeftPullAction!!.targetTriggerOffset) / pullRate).toInt()
                consumed[0] += xConsumed
                dx -= xConsumed
                hOffset = mLeftPullAction!!.targetTriggerOffset
            }
            setHorOffsetToTargetOffsetHelper(hOffset)
        }
        return dx
    }

    private fun checkEdgeRightScrollRight(dx: Int, consumed: IntArray, type: Int): Int {
        var dx = dx
        if (dx > 0 && isEdgeEnabled(PULL_EDGE_RIGHT) && !mTargetView!!.canScrollHorizontally(1) &&
            (type == ViewCompat.TYPE_TOUCH || mRightPullAction!!.isNeedReceiveFlingFromTargetView)
        ) {
            var hOffset: Int = mTargetOffsetHelper!!.leftAndRightOffset
            val pullRate =
                if (type == ViewCompat.TYPE_TOUCH) mRightPullAction!!.pullRate else mRightPullAction!!.getFlingRate(
                    -hOffset
                )
            val rx = (dx * pullRate).toInt()
            if (rx == 0) {
                return dx
            }
            if (mRightPullAction!!.isCanOverPull || hOffset - rx >= -mRightPullAction!!.targetTriggerOffset) {
                hOffset -= rx
                consumed[0] += dx
                dx = 0
            } else {
                val xConsumed =
                    ((-mRightPullAction!!.targetTriggerOffset - hOffset) / pullRate).toInt()
                consumed[0] += xConsumed
                dx -= xConsumed
                hOffset = -mRightPullAction!!.targetTriggerOffset
            }
            setHorOffsetToTargetOffsetHelper(hOffset)
        }
        return dx
    }

    private fun checkEdgeRightScrollLeft(dx: Int, consumed: IntArray, type: Int): Int {
        var dx = dx
        var hOffset: Int = mTargetOffsetHelper!!.leftAndRightOffset
        if (dx < 0 && isEdgeEnabled(PULL_EDGE_RIGHT) && hOffset < 0) {
            val pullRate = if (type == ViewCompat.TYPE_TOUCH) mRightPullAction!!.pullRate else 1f
            val rx = (dx * pullRate).toInt()
            if (rx == 0) {
                return dx
            }
            if (hOffset <= dx) {
                consumed[0] += dx
                hOffset -= rx
                dx = 0
            } else {
                val xConsumed = (hOffset / pullRate).toInt()
                consumed[0] += xConsumed
                dx -= xConsumed
                hOffset = 0
            }
            setHorOffsetToTargetOffsetHelper(hOffset)
        }
        return dx
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams): FrameLayout.LayoutParams {
        return LayoutParams(lp)
    }

    override fun generateLayoutParams(attrs: AttributeSet): FrameLayout.LayoutParams {
        return LayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): FrameLayout.LayoutParams {
        return LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams && super.checkLayoutParams(p)
    }

    class LayoutParams : FrameLayout.LayoutParams {
        var isTarget = false
        var edge = PULL_EDGE_TOP
        var targetTriggerOffset = WRAP_CONTENT
        var canOverPull = false
        var pullRate = DEFAULT_PULL_RATE
        var needReceiveFlingFromTarget = true
        var receivedFlingFraction = DEFAULT_FLING_FRACTION
        var actionInitOffset = 0
        var scrollSpeedPerPixel = DEFAULT_SCROLL_SPEED_PER_PIXEL
        var triggerUntilScrollToTriggerOffset = false
        var scrollToTriggerOffsetAfterTouchUp = true

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs) {
            val a = c.obtainStyledAttributes(
                attrs,
                R.styleable.QMUIPullLayout_Layout
            )
            isTarget = a.getBoolean(R.styleable.QMUIPullLayout_Layout_qmui_is_target, false)
            if (!isTarget) {
                edge = a.getInteger(R.styleable.QMUIPullLayout_Layout_qmui_pull_edge, PULL_EDGE_TOP)
                try {
                    targetTriggerOffset = a.getDimensionPixelSize(
                        R.styleable.QMUIPullLayout_Layout_qmui_target_view_trigger_offset,
                        WRAP_CONTENT
                    )
                } catch (ignore: Exception) {
                    val intValue = a.getInt(
                        R.styleable.QMUIPullLayout_Layout_qmui_target_view_trigger_offset,
                        WRAP_CONTENT
                    )
                    if (intValue == WRAP_CONTENT) {
                        targetTriggerOffset = WRAP_CONTENT
                    }
                }
                canOverPull = a.getBoolean(
                    R.styleable.QMUIPullLayout_Layout_qmui_can_over_pull, false
                )
                pullRate = a.getFloat(
                    R.styleable.QMUIPullLayout_Layout_qmui_pull_rate, pullRate
                )
                needReceiveFlingFromTarget = a.getBoolean(
                    R.styleable.QMUIPullLayout_Layout_qmui_need_receive_fling_from_target_view, true
                )
                receivedFlingFraction = a.getFloat(
                    R.styleable.QMUIPullLayout_Layout_qmui_received_fling_fraction,
                    receivedFlingFraction
                )
                actionInitOffset = a.getDimensionPixelSize(
                    R.styleable.QMUIPullLayout_Layout_qmui_action_view_init_offset,
                    0
                )
                scrollSpeedPerPixel = a.getFloat(
                    R.styleable.QMUIPullLayout_Layout_qmui_scroll_speed_per_pixel,
                    scrollSpeedPerPixel
                )
                triggerUntilScrollToTriggerOffset = a.getBoolean(
                    R.styleable.QMUIPullLayout_Layout_qmui_trigger_until_scroll_to_trigger_offset,
                    false
                )
                scrollToTriggerOffsetAfterTouchUp = a.getBoolean(
                    R.styleable.QMUIPullLayout_Layout_qmui_scroll_to_trigger_offset_after_touch_up,
                    true
                )
            }
            a.recycle()
        }

        constructor(width: Int, height: Int) : super(width, height) {}
        constructor(p: ViewGroup.LayoutParams?) : super(p!!) {}
        constructor(source: MarginLayoutParams?) : super(source!!) {}
    }

    class PullAction internal constructor(
        val mActionView: View,
        private val mTargetTriggerOffset: Int,
        val isCanOverPull: Boolean,
        val pullRate: Float,
        val actionInitOffset: Int,
        @field:PullEdge @get:PullEdge
        @param:PullEdge val pullEdge: Int,
        val scrollSpeedPerPixel: Float,
        val isNeedReceiveFlingFromTargetView: Boolean,
        private val mReceivedFlingFraction: Float,
        val isTriggerUntilScrollToTriggerOffset: Boolean,
        val isScrollToTriggerOffsetAfterTouchUp: Boolean,
        private val mActionViewOffsetCalculator: ActionViewOffsetCalculator?
    ) {

        val mViewOffsetHelper: QMUIViewOffsetHelper = QMUIViewOffsetHelper(mActionView)

        var mIsActionRunning = false
        val actionPullSize: Int
            get() = if (pullEdge == PULL_EDGE_TOP || pullEdge == PULL_EDGE_BOTTOM) {
                mActionView.height
            } else mActionView.width
        val targetTriggerOffset: Int
            get() {
                return if (mTargetTriggerOffset == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    actionPullSize - actionInitOffset * 2
                } else mTargetTriggerOffset
            }

        fun getFlingRate(currentTargetOffset: Int): Float {
            return Math.min(
                pullRate,
                Math.max(
                    pullRate - (currentTargetOffset - targetTriggerOffset) * mReceivedFlingFraction,
                    0f
                )
            )
        }

        fun updateOffset(offset: Int) {
            if (pullEdge == PULL_EDGE_LEFT) {
                mViewOffsetHelper.setLeftAndRightOffset(offset)
            } else if (pullEdge == PULL_EDGE_TOP) {
                mViewOffsetHelper.setTopAndBottomOffset(offset)
            } else if (pullEdge == PULL_EDGE_RIGHT) {
                mViewOffsetHelper.setLeftAndRightOffset(-offset)
            } else {
                mViewOffsetHelper.setTopAndBottomOffset(-offset)
            }
        }

        fun onTargetMoved(targetOffset: Int) {
            updateOffset(
                mActionViewOffsetCalculator!!.calculateOffset(this, targetOffset)
            )
        }

        init {
            updateOffset(actionInitOffset)
        }
    }

    class PullActionBuilder(
        val mActionView: View,
        @field:PullEdge @param:PullEdge val mPullEdge: Int
    ) {
        private var mTargetTriggerOffset = ViewGroup.LayoutParams.WRAP_CONTENT
        private var mCanOverPull = false
        private var mPullRate = DEFAULT_PULL_RATE
        private var mNeedReceiveFlingFromTargetView = true
        private var mReceivedFlingFraction = DEFAULT_FLING_FRACTION
        private var mActionInitOffset = 0
        private var mScrollSpeedPerPixel = DEFAULT_SCROLL_SPEED_PER_PIXEL
        private var mActionViewOffsetCalculator: ActionViewOffsetCalculator? = null
        private var mTriggerUntilScrollToTriggerOffset = false
        private var mScrollToTriggerOffsetAfterTouchUp = true
        fun triggerUntilScrollToTriggerOffset(triggerUntilScrollToTriggerOffset: Boolean): PullActionBuilder {
            mTriggerUntilScrollToTriggerOffset = triggerUntilScrollToTriggerOffset
            return this
        }

        fun scrollToTriggerOffsetAfterTouchUp(scrollToTriggerOffsetAfterTouchUp: Boolean): PullActionBuilder {
            mScrollToTriggerOffsetAfterTouchUp = scrollToTriggerOffsetAfterTouchUp
            return this
        }

        fun targetTriggerOffset(offset: Int): PullActionBuilder {
            mTargetTriggerOffset = offset
            return this
        }

        fun canOverPull(canOverPull: Boolean): PullActionBuilder {
            mCanOverPull = canOverPull
            return this
        }

        fun receivedFlingFraction(fraction: Float): PullActionBuilder {
            mReceivedFlingFraction = fraction
            return this
        }

        fun needReceiveFlingFromTargetView(needReceive: Boolean): PullActionBuilder {
            mNeedReceiveFlingFromTargetView = needReceive
            return this
        }

        fun pullRate(rate: Float): PullActionBuilder {
            mPullRate = rate
            return this
        }

        fun scrollSpeedPerPixel(scrollSpeedPerPixel: Float): PullActionBuilder {
            mScrollSpeedPerPixel = scrollSpeedPerPixel
            return this
        }

        fun actionInitOffset(initOffset: Int): PullActionBuilder {
            mActionInitOffset = initOffset
            return this
        }

        fun actionViewOffsetCalculator(calculator: ActionViewOffsetCalculator?): PullActionBuilder {
            mActionViewOffsetCalculator = calculator
            return this
        }

        fun build(): PullAction {
            if (mActionViewOffsetCalculator == null) {
                mActionViewOffsetCalculator = QMUIAlwaysFollowOffsetCalculator()
            }
            return PullAction(
                mActionView,
                mTargetTriggerOffset,
                mCanOverPull,
                mPullRate,
                mActionInitOffset,
                mPullEdge,
                mScrollSpeedPerPixel,
                mNeedReceiveFlingFromTargetView,
                mReceivedFlingFraction,
                mTriggerUntilScrollToTriggerOffset,
                mScrollToTriggerOffsetAfterTouchUp,
                mActionViewOffsetCalculator
            )
        }
    }

    interface ActionViewOffsetCalculator {
        fun calculateOffset(pullAction: PullAction?, targetOffset: Int): Int
    }

    interface ActionPullWatcherView {
        fun onPull(pullAction: PullAction?, currentTargetOffset: Int)
        fun onActionTriggered()
        fun onActionFinished()
    }

    interface StopTargetViewFlingImpl {
        fun stopFling(view: View?)
    }

    class DefaultStopTargetViewFlingImpl private constructor() : StopTargetViewFlingImpl {
        override fun stopFling(view: View?) {
            if (view is RecyclerView) {
                view.stopScroll()
            }
        }

        companion object {
            private var sInstance: DefaultStopTargetViewFlingImpl? = null
            val instance: DefaultStopTargetViewFlingImpl?
                get() {
                    if (sInstance == null) {
                        sInstance = DefaultStopTargetViewFlingImpl()
                    }
                    return sInstance
                }
        }
    }

    interface ActionListener {
        fun onActionTriggered(pullAction: PullAction)
    }


}