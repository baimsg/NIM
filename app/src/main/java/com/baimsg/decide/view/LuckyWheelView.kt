package com.baimsg.decide.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.baimsg.decide.R

/**
 * Create by Baimsg on 2022/6/9
 *
 **/
class LuckyWheelView : View {
    private var awardsList: List<AwardModel>? = ArrayList()
    private var itemTitleTextPaint: Paint? = null
    private var itemContentTextPaint: Paint? = null
    private var circleLinePaint: Paint? = null
    private var intervalLinePaint: Paint? = null
    private var unSelectPiecePaint: Paint? = null
    private var selectedPiecePaint: Paint? = null
    private val arrowPaint: Paint? = null
    private var viewWidth = 0
    private var viewHeight = 0
    private var mPaddingLeft = 0
    private var mPaddingTop = 0
    private var mPaddingRight = 0
    private var mPaddingBottom = 0
    private val startAngle = 0
    private val radius = 0
    private val eachPieceEqual = true
    private var lastAngle = 0
    private var lastPos = 0
    private var rotationSpeed = 300
    private var isRotationing = false
    private var itemTitleColor = Color.BLACK
    private var itemContentColor = Color.BLUE
    private var lineColor = Color.BLUE
    private var pieceUnSelectColor = Color.YELLOW
    private var pieceSelectedColor = Color.WHITE
    private var itemTitleTextSize = 70
    private var itemContentTextSize = 40
    private var intervalStrokeWidth = 10
    private var circleStrokeWidth = 20

    //    private int[] countList = {0, 0, 0, 0, 0, 0};
    fun setAwardsList(awardsList: List<AwardModel>?) {
        this.awardsList = awardsList
    }

    fun setItemTitleColor(itemTitleColor: Int) {
        this.itemTitleColor = itemTitleColor
    }

    fun setItemContentColor(itemContentColor: Int) {
        this.itemContentColor = itemContentColor
    }

    fun setLineColor(lineColor: Int) {
        this.lineColor = lineColor
    }

    fun setPieceUnSelectColor(pieceUnSelectColor: Int) {
        this.pieceUnSelectColor = pieceUnSelectColor
    }

    fun setPieceSelectedColor(pieceSelectedColor: Int) {
        this.pieceSelectedColor = pieceSelectedColor
    }

    fun setItemTitleTextSize(itemTitleTextSize: Int) {
        this.itemTitleTextSize = itemTitleTextSize
    }

    fun setItemContentTextSize(itemContentTextSize: Int) {
        this.itemContentTextSize = itemContentTextSize
    }

    fun setIntervalStrokeWidth(intervalStrokeWidth: Int) {
        this.intervalStrokeWidth = intervalStrokeWidth
    }

    fun setCircleStrokeWidth(circleStrokeWidth: Int) {
        this.circleStrokeWidth = circleStrokeWidth
    }

    fun setRotationSpeed(rotationSpeed: Int) {
        this.rotationSpeed = rotationSpeed
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(attrs)
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttrs(attrs)
        init()
    }

    private fun initAttrs(attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.LuckyWheelView)
        intervalStrokeWidth = array.getDimension(
            R.styleable.LuckyWheelView_interval_line_width, Dp2Px(
                context, 5
            ).toFloat()
        ).toInt()
        circleStrokeWidth = array.getDimension(
            R.styleable.LuckyWheelView_circle_line_width, Dp2Px(
                context, 10
            ).toFloat()
        ).toInt()
        itemTitleTextSize = array.getDimension(
            R.styleable.LuckyWheelView_item_title_text_size, Sp2Px(
                context, 15
            ).toFloat()
        ).toInt()
        itemContentTextSize = array.getDimension(
            R.styleable.LuckyWheelView_item_content_text_size, Sp2Px(
                context, 15
            ).toFloat()
        ).toInt()
        itemTitleColor = array.getColor(R.styleable.LuckyWheelView_item_title_color, Color.BLACK)
        itemContentColor =
            array.getColor(R.styleable.LuckyWheelView_item_content_color, Color.BLACK)
        lineColor = array.getColor(R.styleable.LuckyWheelView_line_color, Color.BLACK)
        pieceUnSelectColor =
            array.getColor(R.styleable.LuckyWheelView_un_select_color, Color.YELLOW)
        pieceSelectedColor = array.getColor(R.styleable.LuckyWheelView_selected_color, Color.WHITE)
        array.recycle()
    }

    private fun init() {
        itemTitleTextPaint = Paint()
        itemTitleTextPaint!!.isAntiAlias = true
        itemTitleTextPaint!!.isDither = true
        itemTitleTextPaint!!.textSize = itemTitleTextSize.toFloat()
        itemTitleTextPaint!!.color = itemTitleColor
        itemContentTextPaint = Paint()
        itemContentTextPaint!!.isAntiAlias = true
        itemContentTextPaint!!.isDither = true
        itemContentTextPaint!!.textSize = itemContentTextSize.toFloat()
        itemContentTextPaint!!.color = itemContentColor
        unSelectPiecePaint = Paint()
        unSelectPiecePaint!!.isAntiAlias = true
        unSelectPiecePaint!!.isDither = true
        unSelectPiecePaint!!.color = pieceUnSelectColor
        selectedPiecePaint = Paint()
        selectedPiecePaint!!.isAntiAlias = true
        selectedPiecePaint!!.isDither = true
        selectedPiecePaint!!.color = pieceSelectedColor
        intervalLinePaint = Paint()
        intervalLinePaint!!.isAntiAlias = true
        intervalLinePaint!!.isDither = true
        intervalLinePaint!!.color = lineColor
        intervalLinePaint!!.strokeWidth = intervalStrokeWidth.toFloat()
        intervalLinePaint!!.style = Paint.Style.STROKE
        circleLinePaint = Paint()
        circleLinePaint!!.isAntiAlias = true
        circleLinePaint!!.isDither = true
        circleLinePaint!!.color = lineColor
        circleLinePaint!!.strokeWidth = circleStrokeWidth.toFloat()
        circleLinePaint!!.style = Paint.Style.STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWidth = measuredWidth
        viewHeight = measuredHeight
        mPaddingLeft = getPaddingLeft()
        mPaddingTop = getPaddingTop()
        mPaddingRight = getPaddingRight()
        mPaddingBottom = getPaddingBottom()

//        viewWidth = viewWidth - paddingLeft - paddingRight;
//        viewHeight = viewHeight - paddingBottom - paddingTop;
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

//        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
//            setMeasuredDimension(widthSize, heightSize);
//        } else if (widthMode == MeasureSpec.EXACTLY) {
//            setMeasuredDimension(widthSize, maxContentHeight);
//        } else if (heightMode == MeasureSpec.EXACTLY) {
//            setMeasuredDimension(maxContentWidth, heightSize);
//        } else {
//            setMeasuredDimension(maxContentWidth, maxContentHeight);
//        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (null != awardsList && awardsList!!.size > 0) {
            val rectF = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
            val outRadius =
                (Math.min(viewWidth / 2, viewHeight / 2) - circleStrokeWidth / 2).toFloat()
            val intRadius = Math.min(viewWidth / 4, viewHeight / 4).toFloat()
            val ringWidth = outRadius - intRadius
            val outRect = RectF(
                viewWidth / 2 - outRadius,
                viewHeight / 2 - outRadius,
                outRadius + viewWidth / 2,
                outRadius + viewHeight / 2
            )
            val inRect = RectF(0f, 0f, intRadius * 2, intRadius * 2)
            canvas.drawCircle(
                (viewWidth / 2).toFloat(), (viewHeight / 2).toFloat(), outRadius - 2,
                unSelectPiecePaint!!
            )
            if (!isRotationing) {
                canvas.drawArc(outRect, 240f, 60f, true, selectedPiecePaint!!)
            }
            canvas.drawCircle(
                (viewWidth / 2).toFloat(), (viewHeight / 2).toFloat(), outRadius,
                circleLinePaint!!
            )
            //            canvas.drawCircle(viewWidth / 2, viewHeight / 2, intRadius, intervalLinePaint);

//            canvas.drawRect(outRect, intervalLinePaint);
            val awardsSize = awardsList!!.size
            val percentAngle = 360 / awardsSize
            canvas.rotate(
                lastAngle.toFloat(),
                (viewWidth / 2).toFloat(),
                (viewHeight / 2).toFloat()
            )
            canvas.save()
            for (i in 0 until awardsSize) {
                if (i != 0) {
                    canvas.rotate(
                        percentAngle.toFloat(),
                        (viewWidth / 2).toFloat(),
                        (viewHeight / 2).toFloat()
                    )
                }
                canvas.drawLine(
                    viewWidth / 2 - outRadius,
                    (viewHeight / 2).toFloat(),
                    (viewWidth / 2).toFloat(),
                    (viewHeight / 2).toFloat(),
                    intervalLinePaint!!
                )
                val awardTitleStr: String = awardsList!![i].title
                val awardContentStr: String = awardsList!![i].content
                val titleWidth = itemTitleTextPaint!!.measureText(awardTitleStr)
                val contentWidth = itemTitleTextPaint!!.measureText(awardContentStr)
                canvas.drawText(
                    awardTitleStr,
                    viewWidth / 2 - titleWidth / 2,
                    viewHeight / 2 - outRadius + ringWidth / 2,
                    itemTitleTextPaint!!
                )
                canvas.drawText(
                    awardContentStr,
                    viewWidth / 2 - contentWidth / 2,
                    viewHeight / 2 - outRadius + ringWidth - 30,
                    itemTitleTextPaint!!
                )
            }
            canvas.restore()
        }
    }

    //根据每项百分比来转
    fun startPreSetRotation() {
        for (i in awardsList!!.indices) {
            val randomResult = Math.random()
            if (randomResult < awardsList!![i].percent) {
                startRotation(awardsList!![i].percent)
                return
            } else {
                continue
            }
        }
        //循环结局还是没击中，再来一波
        startPreSetRotation()
    }

    //pos=-1,随机旋转  pos=x,旋转到x
    fun startRotation(pos: Int) {
        if (isRotationing) {
            return
        }
        //        Logger.e("startRotation pos=" + pos);
        if (pos < 0 || pos > awardsList!!.size) {
            val newPos = (Math.random() * 10).toInt()
            startRotation(newPos)
            return
        }
        isRotationing = true
        val lap = (Math.random() * 12).toInt() + 4
        var angle = 0
        //        Logger.e("lastPos=" + lastPos);
        angle = if (pos > lastPos) {
            (awardsList!!.size - pos + lastPos) * 60
        } else if (pos < lastPos) {
            (lastPos - pos) * 60
        } else {
            0
        }
        val increaseDegree = lap * 360 + angle
        val time = ((lap + angle / 360) * rotationSpeed).toLong()
        val DesRotate = increaseDegree + lastAngle

//        //TODO 为了每次都能旋转到转盘的中间位置
//        int offRotate = DesRotate % 360 % 60;
//        DesRotate -= offRotate;
//        DesRotate += 30;
        val valueAnimator = ValueAnimator.ofInt(lastAngle, DesRotate)
        valueAnimator.duration = time
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.start()
        valueAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            lastAngle = (value % 360 + 360) % 360
            //                LuckyWheelView.this.setRotation(lastAngle);
            postInvalidate()
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                //                Logger.e("lastAngle=" + lastAngle);
                isRotationing = false
                lastPos = pos
            }
        })
    }

    private fun queryPosition(): Int {
        lastAngle = (lastAngle % 360 + 360) % 360
        val pos = lastAngle / 60
        return calcumAngle(pos)
    }

    private fun calcumAngle(pos: Int): Int {
        var pos = pos
        pos = if (pos >= 0 && pos <= 3) {
            3 - pos
        } else {
            6 - pos + 3
        }
        //        Logger.e("pos=" + pos);
        return pos
    }

    companion object {
        //dpi转px
        fun Dp2Px(context: Context, dpi: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpi.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }

        //px转dp
        fun Px2Dp(context: Context, px: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                px.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }

        //sp转px
        fun Sp2Px(context: Context, sp: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                sp.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }

        //px转sp
        fun Px2Sp(context: Context, px: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                px.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }
    }
}
