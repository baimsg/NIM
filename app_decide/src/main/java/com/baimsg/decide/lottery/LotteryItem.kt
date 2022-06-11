package com.baimsg.decide.lottery

import android.graphics.Bitmap
import android.text.TextUtils

/**
 * Create by Baimsg on 2022/6/10
 *
 **/
class LotteryItem(name: String, bitmap: Bitmap, color: Int) : LotterySpanView.Lottery {
    private val mName: String
    private val mBitmap: Bitmap
    private val mBackground: Int
    override fun getName(): String {
        return mName
    }

    override fun getIconImage(): Bitmap {
        return mBitmap
    }

    override fun getBackground(): Int {
        return mBackground
    }

    override fun isEquals(lottery: LotterySpanView.Lottery): Boolean {
        return TextUtils.equals(mName, lottery.name)
    }

    init {
        mName = name
        mBitmap = bitmap
        mBackground = color
    }
}
