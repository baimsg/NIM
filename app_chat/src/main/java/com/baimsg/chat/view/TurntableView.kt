package com.baimsg.chat.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.baimsg.chat.R
import com.baimsg.chat.util.extensions.dp2px
import java.util.*


/**
 * Create by Baimsg on 2022/6/10
 *
 **/
class TurntableView @JvmOverloads constructor(
    mContext: Context,
    val attrs: AttributeSet? = null,
    val defStyleAttr: Int = 0
) : View(mContext, attrs, defStyleAttr) {

    private val viewConfiguration by lazy {
        ViewConfiguration.get(context)
    }

    var colors = intArrayOf(
        Color.parseColor("#479CE5"),
        Color.parseColor("#E38AB4"),
        Color.parseColor("#7766cc"),
        Color.parseColor("#D12D7D"),
        Color.parseColor("#D2DA47"),
        Color.parseColor("#76BCAC"),
        Color.parseColor("#D32D25"),
        Color.parseColor("#E18832"),
        Color.parseColor("#AECA41"),
        Color.parseColor("#d75adf")
    )

    /**
     * 背景颜色
     */
    var turntableBackgroundShadow = 0

    /**
     * 背景阴影
     */
    var turntableSelectShadow = 0

    /**
     * 动画监听器
     */
    val animatorListener: Animator.AnimatorListener by lazy {
        object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animator: Animator) {
                setFling(true)
            }
        }
    }

    var C: Bitmap? = null
    var D: OnChangeListener? = null


    val f3703a by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
        }
    }

    var f3704b = context.dp2px(10.0f)

    var f3705c = context.dp2px(8.0f)

    var f3706d: RectF = RectF()
    var e1: RectF = RectF()
    var f: MutableList<TurntableData> = mutableListOf()

    var animatedValue = 0f
    var h = context.dp2px(1.5f)

    val valueAnimator1: ValueAnimator by lazy {
        ValueAnimator().apply {
            interpolator = e()
            addUpdateListener(TurntableViewAnimatorUpdateListener(this@TurntableView))
            addListener(animatorListener)
        }
    }

    var j: VelocityTracker? = null
    var k = false
    val l: Scroller by lazy {
        Scroller(context).apply {
            setFriction(ViewConfiguration.getScrollFriction() * 0.5f)
        }
    }
    var m = viewConfiguration.scaledTouchSlop
    var n = viewConfiguration.scaledMinimumFlingVelocity
    var o = viewConfiguration.scaledMaximumFlingVelocity

    var p = 0
    var q = 0
    var r = 0
    var s = 0f
    var t = -1
    var u: TurntableData? = null
    var v = false

    val textPaint1: TextPaint by lazy {
        TextPaint().apply {
            isAntiAlias = true
            color = -1
            style = Paint.Style.FILL
            textSize = resources.getDimensionPixelSize(R.dimen.turntable_text_size).toFloat()
        }
    }

    var mX = context.dp2px(65.0f)
    var mY = context.dp2px(8.0f)
    var turntableBackground = 0


    init {
        val array =
            context.obtainStyledAttributes(attrs, R.styleable.TurntableView, defStyleAttr, 0)
        turntableBackground = array.getColor(
            R.styleable.TurntableView_turntable_background,
            ContextCompat.getColor(context, R.color.turntable_background)
        )
        turntableBackgroundShadow = array.getColor(
            R.styleable.TurntableView_turntable_background_shadow,
            ContextCompat.getColor(context, R.color.turntable_background_shadow)
        )
        turntableSelectShadow = array.getColor(
            R.styleable.TurntableView_turntable_select_shadow,
            ContextCompat.getColor(context, R.color.turntable_select_shadow)
        )
        array.recycle()

        setLayerType(LAYER_TYPE_SOFTWARE, null)

        if (isInEditMode) {
            val arrayList = mutableListOf(
                TurntableItem("1"),
                TurntableItem("2"),
                TurntableItem("3"),
                TurntableItem("4"),
                TurntableItem("5"),
                TurntableItem("6")
            )
            setTurntableItems(arrayList)
        }
    }


    class TurntableData {

        var turntableItem: TurntableItem? = null

        var f3709b = 0

        var f3710c = 0f

        var f3711d = 0f

        var e: d? = null

        var isSelect = false
    }


    class d(
        var f3712a: StaticLayout
    ) {
        var f3713b = RectF()
    }

    class e : Interpolator {
        override fun getInterpolation(f: Float): Float {
            val a2 = a(f) * f3714a
            return if (a2 > 0.0f) a2 + f3715b else a2
        }

        companion object {
            val f3714a = 1.0f / a(1.0f)

            val f3715b = 1.0f - a(1.0f) * f3714a
            fun a(f: Float): Float {
                val f2 = f * 8.0f
                return if (f2 < 1.0f) f2 - (1.0f - Math.exp((-f2).toDouble())
                    .toFloat()) else (1.0f - Math.exp((1.0f - f2).toDouble())
                    .toFloat()) * 0.63212055f + 0.36787945f
            }
        }
    }


    private fun getRandomRotate(): Float {
        val random: Random
        if (f.isEmpty()) {
            random = Random()
        } else {
            var f2 = 0.0f
            for (bVar in f) {
                if (!bVar.isSelect) {
                    f2 = f2 + bVar.f3711d - bVar.f3710c
                }
            }
            if (f2 > 0.0f) {
                var nextFloat: Float = Random().nextFloat() * f2
                for (bVar2 in f) {
                    if (bVar2.isSelect) {
                        nextFloat = nextFloat + bVar2.f3711d - bVar2.f3710c
                    } else if (nextFloat >= bVar2.f3710c && nextFloat < bVar2.f3711d) {
                        val f3 = 270.0f - nextFloat - animatedValue
                        return if (f3 < 0.0f) f3 + 360.0f else f3
                    }
                }
            }
            for (bVar3 in f) {
                bVar3.isSelect = false
            }
            random = Random()
        }
        return random.nextInt(360).toFloat()
    }

    fun setFling(i2: Int, i3: Int): Float {
        val width = i2.toFloat() - width.toFloat() / 2.0f
        val height = i3.toFloat() - height.toFloat() / 2.0f
        val i4 = if (width > 0.0f) 1 else if (width == 0.0f) 0 else -1
        if (i4 == 0) {
            return if (height > 0.0f) 90.0f else 270.0f
        }
        val i5 = if (height > 0.0f) 1 else if (height == 0.0f) 0 else -1
        if (i5 == 0) {
            return if (i4 > 0) 0.0f else 180.0f
        }
        if (i4 > 0 && i5 > 0) {
            return Math.toDegrees(Math.atan((height / width).toDouble())).toFloat()
        }
        val i6 = if (width > 0.0f) 1 else if (width == 0.0f) 0 else -1
        if (i6 < 0 && i5 > 0) {
            return 180.0f - Math.toDegrees(Math.atan((height / -width).toDouble())).toFloat()
        }
        if (i6 < 0 && height < 0.0f) {
            return Math.toDegrees(Math.atan((-height / -width).toDouble())).toFloat() + 180.0f
        }
        return if (i4 <= 0 || height >= 0.0f) {
            0.0f
        } else 360.0f - Math.toDegrees(Math.atan((-height / width).toDouble())).toFloat()
    }

    fun setFling(bVar: TurntableData, str: String): StaticLayout {
        val width = (f3706d.width() / 2.0f - mX - context.dp2px(6.0f)).toInt()
        val i2 = if (width < 0) 0 else width
        val f2 = bVar.f3711d - bVar.f3710c
        var dimensionPixelSize =
            resources.getDimensionPixelSize(R.dimen.turntable_text_size).toFloat()
        if (f2 < 30.0f) {
            dimensionPixelSize -= (dimensionPixelSize - resources.getDimensionPixelSize(R.dimen.turntable_small_text_size)
                .toFloat()) * ((30.0f - Math.max(10.0f, f2)) / 20.0f)
        }
        val textPaint = TextPaint()
        textPaint.set(textPaint1)
        textPaint.textSize = dimensionPixelSize
        return if (Build.VERSION.SDK_INT >= 23) StaticLayout.Builder.obtain(
            str,
            0,
            str.length,
            textPaint,
            i2
        ).setAlignment(Layout.Alignment.ALIGN_NORMAL).setMaxLines(2).build() else StaticLayout(
            str,
            textPaint,
            i2,
            Layout.Alignment.ALIGN_NORMAL,
            1.0f,
            0.0f,
            false
        )
    }

    /**
     * 开始旋转动画
     */
    fun startRotateAnimator() {
        if (!l.isFinished) {
            Log.d("TurntableView", "autoRotate not effect since scroller is running")
            return
        }
        v = false
        val randomRotate = getRandomRotate()
        val valueAnimator = valueAnimator1
        val f2 = animatedValue
        valueAnimator.setFloatValues(f2, f2 + 1800.0f + randomRotate)
        valueAnimator1.duration = 6000L
        valueAnimator1.start()
    }

    fun setFling(i2: Int) {
        val nextFloat = ((Random().nextFloat() * 0.2f + 1.8f) * i2.toFloat()).toInt()
        Log.d("TurntableView", "fling: $nextFloat")
        r = 0
        l.fling(scrollX, r, 0, nextFloat, 0, 0, Int.MIN_VALUE, Int.MAX_VALUE)
        ViewCompat.postInvalidateOnAnimation(this)
    }

    /**
     *
     */
    fun setFling(z2: Boolean) {
        var z3 = true
        v = true
        val bVar = u
        if (bVar != null) {
            bVar.isSelect = true
            if (D != null) {
                var i2 = 0
                for (bVar2 in f) {
                    if (!bVar2.isSelect) {
                        i2++
                    }
                }
                val cVar = D
                val fVar = u!!.turntableItem
//                val mVar = cVar as m?
//                mVar.f890a.f881d.setText(fVar?.text))
//                mVar.f890a.h.setText(
//                    java.lang.String.format(
//                        Locale.getDefault(),
//                        "剩余%d项",
//                        Integer.valueOf(i2)
//                    )
//                )
//                b.a.n.n.o.b.a("turntable_complete_101", if (z2) "自动" else "手动")
//                mVar.f890a.g.setEnabled(true)
                if (i2 == 0) {
//                    val jVar = j(mVar.f890a.getActivity(), fVar.getText())
//                    jVar.setOnDismissListener(l(mVar, jVar))
//                    jVar.show()
//                    b.a.n.n.o.b.d("turntable_result_dialog_show187")
                }
//                if (animatedValue.d()) {
//                    if (TextUtils.isEmpty(fVar.getText()) || mVar.f890a.k || !animatedValue.e()) {
//                        z3 = false
//                    }
//                    e1.a.e.c.a().a(mVar.f890a.getContext(), `R$raw`.task_completed, false)
//                    if (z3) {
//                        mVar.f890a.j.a(fVar.getText(), fVar.getText())
//                    }
//                }
            }
        }
        postInvalidateOnAnimation()
    }

    fun b() {
        val createBitmap = Bitmap.createBitmap(
            1.coerceAtLeast(measuredWidth), 1.coerceAtLeast(measuredHeight), Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(createBitmap)
        f3703a.style = Paint.Style.FILL
        f3703a.color = turntableBackground
        f3703a.setShadowLayer(
            context.dp2px(4.0f).toFloat(),
            0.0f,
            0.0f,
            turntableBackgroundShadow
        )
        canvas.drawCircle(
            width.toFloat() / 2.0f,
            height.toFloat() / 2.0f,
            f3706d.width() / 2.0f + f3705c.toFloat(),
            f3703a
        )
        f3703a.setShadowLayer(0.0f, 0.0f, 0.0f, 0)
        C = createBitmap
    }

    fun c() {
        if (animatedValue >= 0.0f) {
            while (true) {
                val f2 = animatedValue
                if (f2 < 360.0f) {
                    break
                }
                animatedValue = f2 - 360.0f
            }
        } else {
            while (true) {
                val f3 = animatedValue
                if (f3 >= 0.0f) {
                    break
                }
                animatedValue = f3 + 360.0f
            }
        }
        var bVar: TurntableData? = null
        var f4 = 270.0f - animatedValue
        if (f4 < 0.0f) {
            f4 += 360.0f
        }
        val it: Iterator<TurntableData> = f.iterator()
        while (true) {
            if (!it.hasNext()) {
                break
            }
            val next = it.next()
            if (f4 >= next.f3710c && f4 < next.f3711d) {
                bVar = next
                break
            }
        }
        if (bVar == null) {
            Log.e("TurntableView", "do not found item")
        } else if (u !== bVar) {
            u = bVar
            val cVar = D
//            if (cVar != null) {
//                val mVar: m = cVar as m
//                mVar.f890a.f881d.setText(bVar.f3708a.getText())
//                if (animatedValue.d()) {
//                    e1.a.e.c.a().a(mVar.f890a.getContext(), `R$raw`.turntable_tick, false)
//                }
//            }
        }
    }

    override fun computeScroll() {
        if (!l.isFinished) {
            l.computeScrollOffset()
            r = l.currY
            animatedValue = r.toFloat() * 1.0f / ((f3706d.width() / 2.0f).toInt()
                .toDouble() * 6.283185307179586).toInt()
                .toFloat() * 360.0f
            c()
//            e1.a.e.a.a("TurntableView", "computeScroll: $r, $animatedValue")
            if (!l.isFinished) {
                ViewCompat.postInvalidateOnAnimation(this)
            } else {
                setFling(false)
            }
        }
    }

    fun getLeftCount(): Int {
        var i2 = 0
        for (bVar in f) {
            if (!bVar.isSelect) {
                i2++
            }
        }
        return i2
    }

    fun getSelectedCount(): Int {
        var i2 = 0
        for (bVar in f) {
            if (bVar.isSelect) {
                i2++
            }
        }
        return i2
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (valueAnimator1.isRunning) {
            valueAnimator1.cancel()
        }
    }

    @SuppressLint("DrawAllocation", "WrongConstant")
    override fun onDraw(canvas: Canvas) {
        var staticLayout: StaticLayout
        var rectF: RectF
        var textPaint: TextPaint
        var i2: Int
        var textPaint2: TextPaint
        val currentTimeMillis = System.currentTimeMillis()
        val width = width.toFloat() / 2.0f
        val height = height.toFloat() / 2.0f
        if (C == null) {
            b()
        }
        C?.let { canvas.drawBitmap(it, 0.0f, 0.0f, null as Paint?) }
        canvas.save()
        canvas.rotate(animatedValue, width, height)
        f3703a.style = Paint.Style.FILL
        for (bVar in f) {
            f3703a.color = bVar.f3709b
            val rectF2 = f3706d
            val f2 = bVar.f3710c
            canvas.drawArc(rectF2, f2, bVar.f3711d - f2, true, f3703a)
        }
        canvas.save()
        f3703a.style = Paint.Style.STROKE
        f3703a.strokeWidth = h.toFloat()
        f3703a.color = turntableBackground
        for (i3 in f.indices) {
            val bVar2 = f[i3]
            canvas.drawLine(width, height, f3706d.width() / 2.0f + width, height, f3703a)
            canvas.rotate(bVar2.f3711d - bVar2.f3710c, width, height)
        }
        canvas.restore()
        canvas.save()
        for (i4 in f.indices) {
            val bVar3 = f[i4]
            val f3 = (bVar3.f3711d - bVar3.f3710c) / 2.0f
            canvas.rotate(f3, width, height)
            val dVar = bVar3.e
            if (dVar == null) {
                val a2 = setFling(bVar3, bVar3.turntableItem?.text ?: "")
                bVar3.e = d(a2)
                val lineCount = a2.lineCount
                var f4 = 0.0f
                for (i5 in 0 until lineCount) {
                    f4 = f4.coerceAtLeast(a2.getLineWidth(i5))
                }
                bVar3.e!!.f3713b[0.0f, 0.0f, f4] = a2.height.toFloat()
                staticLayout = a2
                rectF = bVar3.e!!.f3713b
            } else {
                staticLayout = dVar.f3712a
                rectF = dVar.f3713b
            }
            val save: Int = canvas.save()
            canvas.translate(
                (getWidth() - f3704b - f3705c) - rectF.width() - mY,
                height - rectF.height() / 2.0f
            )
            var i6 = -1
            if (!v || bVar3 === u) {
                textPaint = staticLayout.paint
            } else {
                textPaint = staticLayout.paint
                i6 = Color.parseColor("#333333")
            }
            textPaint.color = i6
            if (!bVar3.isSelect || bVar3 === u) {
                textPaint2 = staticLayout.paint
                i2 = staticLayout.paint.flags and -17
            } else {
                textPaint2 = staticLayout.paint
                i2 = 16
            }
            textPaint2.flags = i2
            staticLayout.draw(canvas)
            canvas.restoreToCount(save)
            canvas.rotate(f3, width, height)
        }
        canvas.restore()
        if (v && u != null) {
            f3703a.style = Paint.Style.FILL
            f3703a.color = turntableSelectShadow
            val rectF3 = e1
            val rectF4 = f3706d
            rectF3[rectF4.left - 0.5f, rectF4.top - 0.5f, rectF4.right + 0.5f] =
                rectF4.bottom + 0.5f
            val rectF5 = e1
            val bVar4 = u
            val f5 = bVar4!!.f3711d
            canvas.drawArc(rectF5, f5, 360.0f - (f5 - bVar4.f3710c), true, f3703a)
        }
        canvas.restore()
//        e1.a.e.a.a(
//            "TurntableView",
//            "onDraw time pass: " + (System.currentTimeMillis() - currentTimeMillis)
//        )
    }

    override fun onMeasure(i2: Int, i3: Int) {
        val min = Math.min(MeasureSpec.getSize(i2), MeasureSpec.getSize(i3))
        setMeasuredDimension(min, min)
    }

    override fun onSizeChanged(i2: Int, i3: Int, i4: Int, i5: Int) {
        val min = (i2.coerceAtMost(i3) - f3704b * 2 - f3705c * 2).toFloat()
        val f2 = (i2.toFloat() - min) / 2.0f
        val f3 = (i3.toFloat() - min) / 2.0f
        f3706d[f2, f3, f2 + min] = min + f3
        b()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        val velocityTracker: VelocityTracker?
        var parent: ViewParent
        var i2 = 0
        if (!isEnabled) {
            return false
        }
        if (j == null) {
            j = VelocityTracker.obtain()
        }
        val actionMasked = motionEvent.actionMasked
        val obtain = MotionEvent.obtain(motionEvent)
        if (actionMasked != 0) {
            if (actionMasked != 1) {
                if (actionMasked == 2) {
                    val findPointerIndex = motionEvent.findPointerIndex(t)
                    if (findPointerIndex == -1) {
                        val anima2: StringBuilder = StringBuilder().apply {
                            append("Invalid pointerId=")
                        }
                        anima2.append(t)
                        anima2.append(" in onTouchEvent")
                        Log.e("TurntableView", anima2.toString())
                    } else {
                        val x2 = motionEvent.getX(findPointerIndex).toInt()
                        val y2 = motionEvent.getY(findPointerIndex).toInt()
                        var i3 = q - y2
                        val i4 = p - y2
                        if (Math.abs(i3) >= Math.abs(i4)) {
                            i2 = 1
                        }
                        if (i2 == 0) {
                            i3 = i4
                        }
                        if (!k && Math.abs(i3) > m) {
                            val parent2 = getParent()
                            parent2?.requestDisallowInterceptTouchEvent(true)
                            k = true
                        }
                        if (k) {
                            p = x2
                            q = y2
                            val a3 = setFling(p, q)
                            val f2 = a3 - s
                            Log.d("TurntableView", "newDegree: $a3, deltaDegree: $f2")
                            s = a3
                            animatedValue += f2
                            c()
                        }
                    }
                } else if (actionMasked != 3) {
                    if (actionMasked == 5) {
                        val actionIndex = motionEvent.actionIndex
                        p = motionEvent.getX(actionIndex).toInt()
                        q = motionEvent.getY(actionIndex).toInt()
                        s = setFling(p, q)
                        val anima4: StringBuilder = StringBuilder().apply {
                            append("ACTION_POINTER_DOWN mMotionDegree: ")
                        }
                        anima4.append(s)
                        Log.d("TurntableView", anima4.toString())
                        t = motionEvent.getPointerId(actionIndex)
                    } else if (actionMasked == 6) {
                        val actionIndex2 = motionEvent.actionIndex
                        if (motionEvent.getPointerId(actionIndex2) == t) {
                            if (actionIndex2 == 0) {
                                i2 = 1
                            }
                            p = motionEvent.getX(i2).toInt()
                            q = motionEvent.getY(i2).toInt()
                            s = setFling(p, q)
                            t = motionEvent.getPointerId(i2)
                            val velocityTracker2 = j
                            velocityTracker2?.clear()
                        }
                        p = motionEvent.getX(motionEvent.findPointerIndex(t)).toInt()
                        q = motionEvent.getY(motionEvent.findPointerIndex(t)).toInt()
                    }
                }
                velocityTracker = j
                velocityTracker?.addMovement(obtain)
                obtain.recycle()
                return true
            }
            val velocityTracker3 = j
            velocityTracker3!!.computeCurrentVelocity(1000, o.toFloat())
            val x3 = motionEvent.x.toInt()
            val y3 = motionEvent.y.toInt()
            val xVelocity = velocityTracker3.getXVelocity(t).toInt()
            val yVelocity = velocityTracker3.getYVelocity(t).toInt()
            if (Math.abs(if (Math.abs(yVelocity) >= Math.abs(xVelocity)) yVelocity else xVelocity) >= n) {
                val i5 = yVelocity * yVelocity
                val width = x3.toFloat() - width.toFloat() / 2.0f
                val height = y3.toFloat() - height.toFloat() / 2.0f
                val a5 = setFling(x3, y3)
                val a6 = setFling(xVelocity, yVelocity)
                val f3 = a6 - a5
                Log.d(
                    "TurntableView",
                    "motionDegree: $a5, velocityDegree: $a6, degreeDifference: $f3"
                )
                setFling(
                    (f3706d.width() / 2.0f / Math.sqrt((height * height + width * width).toDouble())
                        .toInt()
                        .toFloat() * (Math.sin(Math.toRadians(f3.toDouble())) * Math.sqrt((i5 + xVelocity * xVelocity).toDouble())
                        .toInt()
                        .toDouble()).toInt().toFloat()).toInt()
                )
            }
            t = -1
            k = false
            val velocityTracker4 = j
            if (velocityTracker4 != null) {
                velocityTracker4.recycle()
                j = null
            }
            velocityTracker = j
            if (velocityTracker != null) {
            }
            obtain.recycle()
            return true
        }
        val z2 = !l.isFinished
        k = z2
        parent = getParent()
        if (z2 && parent != null) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
        if (!l.isFinished) {
            l.abortAnimation()
        }
        if (valueAnimator1.isRunning) {
            valueAnimator1.pause()
        }
        p = motionEvent.x.toInt()
        q = motionEvent.y.toInt()
        s = setFling(p, q)
        t = motionEvent.getPointerId(0)
        v = false
        postInvalidateOnAnimation()
        velocityTracker = j
        if (velocityTracker != null) {
        }
        obtain.recycle()
        return true
    }

    fun setOnChangeListener(onChangeListenerVar: OnChangeListener?) {
        D = onChangeListenerVar
    }

    fun setTurntableItems(list: List<TurntableItem>) {
        var i2: Int
        v = false
        f.clear()
        var i3 = 0
        for (fVar in list) {
            i3 += fVar.weight
        }
        var f2 = 0.0f
        for (i4 in list.indices) {
            val bVar = TurntableData()
            f.add(bVar)
            val fVar2 = list[i4]
            bVar.turntableItem = fVar2
            if (i4 == list.size - 1) {
                val iArr = colors
                if (i4 % iArr.size == 0) {
                    i2 = iArr[5]
                    bVar.f3709b = i2
                    bVar.f3710c = f2
                    bVar.f3711d = bVar.f3710c + fVar2.weight.toFloat() * 360.0f / i3.toFloat()
                    f2 = bVar.f3711d
                }
            }
            val iArr2 = colors
            i2 = iArr2[i4 % iArr2.size]
            bVar.f3709b = i2
            bVar.f3710c = f2
            bVar.f3711d = bVar.f3710c + fVar2.weight.toFloat() * 360.0f / i3.toFloat()
            f2 = bVar.f3711d
        }
        animatedValue = 0.0f
        list.size
        invalidate()
    }

    /**
     * 更改监听器
     */
    interface OnChangeListener
}