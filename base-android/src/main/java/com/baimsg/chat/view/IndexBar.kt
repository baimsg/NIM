package com.baimsg.chat.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Pair
import android.view.MotionEvent
import android.view.View
import com.baimsg.base.android.R

/**
 * Create by Baimsg on 2022/6/30
 *
 **/

class IndexBar @JvmOverloads constructor(
    mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(mContext, attrs, defStyleAttr) {

    private var mCharArray: List<CharSequence>
    private var mTextColorNormal = 0
    private var mTextColorPressed = 0
    private var mTextSizeNormal = 0
    private var mTextSizePressed = 0
    private var mBgColorNormal = 0
    private var mBgColorPressed = 0
    private var mPaintNormal: Paint
    private var mPaintPressed: Paint
    private var mWidth = 0
    private var mHeight = 0
    private var mItemHeight = 0
    private var mLastIndex = -1
    private var mPressed = false
    private var mListener: OnIndexLetterChangedListener? = null

    init {
        //初始化默认属性
        mCharArray = resources.getStringArray(R.array.index_bar_array).map { it.toString() }
        mTextSizeNormal = resources.getDimensionPixelSize(R.dimen.ib_text_size_normal_default)
        mTextSizePressed = resources.getDimensionPixelSize(R.dimen.ib_text_size_pressed_default)
        mTextColorNormal = Color.BLACK
        mTextColorPressed = Color.BLUE
        mBgColorNormal = Color.TRANSPARENT
        mBgColorPressed = Color.TRANSPARENT
        //获取自定义属性
        val ta = context.obtainStyledAttributes(attrs, R.styleable.IndexBar)
        val count = ta.indexCount
        for (i in 0 until count) {
            when (val index = ta.getIndex(i)) {
                R.styleable.IndexBar_text_color_normal -> mTextColorNormal = ta.getColor(
                    index,
                    Color.BLACK
                )
                R.styleable.IndexBar_text_color_pressed -> mTextColorPressed =
                    ta.getColor(
                        index,
                        Color.BLUE
                    )
                R.styleable.IndexBar_bg_color_normal -> mBgColorNormal =
                    ta.getColor(
                        index,
                        Color.TRANSPARENT
                    )
                R.styleable.IndexBar_bg_color_pressed -> mBgColorPressed =
                    ta.getColor(
                        index,
                        Color.TRANSPARENT
                    )
                R.styleable.IndexBar_text_size_normal -> mTextSizeNormal =
                    ta.getDimensionPixelSize(
                        index,
                        context.resources.getDimensionPixelSize(R.dimen.ib_text_size_normal_default)
                    )
                R.styleable.IndexBar_text_size_pressed -> mTextSizePressed =
                    ta.getDimensionPixelSize(
                        index,
                        context.resources.getDimensionPixelSize(R.dimen.ib_text_size_pressed_default)
                    )
            }
        }
        ta.recycle()
        //初始化Paint
        mPaintNormal = Paint()
        mPaintNormal.isAntiAlias = true
        mPaintNormal.color = mTextColorNormal
        mPaintNormal.textSize = mTextSizeNormal.toFloat()
        mPaintPressed = Paint()
        mPaintPressed.isAntiAlias = true
        mPaintPressed.textSize = mTextSizePressed.toFloat()
        mPaintPressed.color = mTextColorPressed
        mPaintPressed.isFakeBoldText = true
        mPaintPressed.typeface = Typeface.DEFAULT_BOLD
        //设置初始背景
        setBackgroundColor(mBgColorNormal)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        mHeight = measuredHeight
        if (mCharArray.isNotEmpty()) mItemHeight =
            (mHeight - paddingTop - paddingBottom) / mCharArray.size
        //如果没有指定具体的宽度，修改宽度为Item高度+paddingLeft+paddingRight
        if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.EXACTLY) mWidth =
            mItemHeight + paddingLeft + paddingRight
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mCharArray.isNotEmpty()) {
            var i = 0
            val length = mCharArray.size
            while (i < length) {
                val c = mCharArray[i]
                var position: Pair<Float, Float>
                if (i == mLastIndex) {
                    position = calPosition(c, mPaintPressed, i)
                    canvas.drawText(
                        c, 0, c.length,
                        position.first, position.second, mPaintPressed
                    )
                } else {
                    position = calPosition(c, mPaintNormal, i)
                    canvas.drawText(
                        c, 0, c.length,
                        position.first, position.second, mPaintNormal
                    )
                }
                i++
            }
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val action = event.action
        val y: Float
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
                updateBgColor(true)
                mListener?.onTouched(true)
                y = event.y
                updateIndex(y)
            }
            MotionEvent.ACTION_MOVE -> {
                y = event.y
                updateIndex(y)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent.requestDisallowInterceptTouchEvent(false)
                updateBgColor(false)
                mListener?.onTouched(false)
                mLastIndex = -1
                invalidate()
            }
        }
        return true
    }

    //更新Index
    private fun updateIndex(y: Float) {
        val curIndex = ((y - paddingTop) / mItemHeight).toInt()
        if (curIndex != mLastIndex) {
            if (curIndex >= 0 && curIndex < mCharArray.size) {
                mListener?.onLetterChanged(
                    mCharArray[curIndex],
                    curIndex,
                    y
                )
                mLastIndex = curIndex
            }
        }
        invalidate()
    }

    //计算位置
    private fun calPosition(str: CharSequence, paint: Paint, index: Int): Pair<Float, Float> {
        // x坐标等于中间-字符串宽度的一半.
        val x = (mWidth - paint.measureText(str.toString())) / 2
        val rect = Rect()
        paint.getTextBounds(str.toString(), 0, str.length, rect)
        val y = (mItemHeight * index + (mItemHeight + rect.height()) / 2 + paddingTop).toFloat()
        return Pair(x, y)
    }

    //更新按压背景
    private fun updateBgColor(pressed: Boolean) {
        if (mPressed != pressed) {
            mPressed = pressed
            if (mPressed) setBackgroundColor(mBgColorPressed) else setBackgroundColor(mBgColorNormal)
        }
    }

    /**
     * 自定义字符数组
     */
    fun setTextArray(array: List<CharSequence>) {
        mCharArray = array
        invalidate()
    }

    /**
     * 设置索引字母改变监听
     */
    fun setOnIndexLetterChangedListener(l: OnIndexLetterChangedListener?) {
        mListener = l
    }

    interface OnIndexLetterChangedListener {
        fun onTouched(touched: Boolean)
        fun onLetterChanged(indexChar: CharSequence, index: Int, y: Float)
    }
}
