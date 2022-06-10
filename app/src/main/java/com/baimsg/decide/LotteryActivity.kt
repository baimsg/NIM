package com.baimsg.decide

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.baimsg.decide.lottery.LotteryItem
import com.baimsg.decide.lottery.LotterySpanView

/**
 * Create by Baimsg on 2022/6/10
 *
 **/
class LotteryActivity : AppCompatActivity(),View.OnClickListener {

    private lateinit var mLotterySpan: LotterySpanView
    private var mBaseSpeed = 12f

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lottery)
        mLotterySpan = findViewById(R.id.lottery_span)
        findViewById<ImageView>(R.id.lottery_start).setOnClickListener(this)
        findViewById<Button>(R.id.lottery_speed_fast).setOnClickListener(this)
        findViewById<Button>(R.id.lottery_speed_middle).setOnClickListener(this)
        findViewById<Button>(R.id.lottery_speed_low).setOnClickListener(this)
        findViewById<Button>(R.id.lottery_damp_fast).setOnClickListener(this)
        findViewById<Button>(R.id.lottery_damp_middle).setOnClickListener(this)
        findViewById<Button>(R.id.lottery_damp_low).setOnClickListener(this)
        onBuildingLotterySpanItems(mLotterySpan)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.lottery_speed_middle -> {
                mBaseSpeed = 12f
            }
            R.id.lottery_speed_fast -> {
                mBaseSpeed = 18f
            }
            R.id.lottery_speed_low -> {
                mBaseSpeed = 6f
            }
            R.id.lottery_damp_middle -> {
                mBaseSpeed = 24f
                mLotterySpan.setDamping(4f)
            }
            R.id.lottery_damp_fast -> {
                mBaseSpeed = 24f
                mLotterySpan.setDamping(5f)
            }
            R.id.lottery_damp_low -> {
                mBaseSpeed = 24f
                mLotterySpan.setDamping(3f)
            }
            R.id.lottery_start -> {
                val index = (Math.random() * mLotterySpan.lotteries.size).toInt()
                if (mBaseSpeed == 24f) {
                    mLotterySpan.onStarting(24f)
                } else {
                    val target = (Math.random() * 6).toFloat() + mBaseSpeed
                    mLotterySpan.onStarting(target)
                }
                mLotterySpan.onStoppingIndex(index)
                Log.v("Lottery", "Stop on" + mLotterySpan.lotteries[index].name)
            }
        }
    }

    private fun onBuildingLotterySpanItems(span: LotterySpanView?) {
        val hundred = BitmapFactory.decodeResource(resources, R.drawable.turntable_packet_hundred)
        val random = BitmapFactory.decodeResource(resources, R.drawable.turntable_packet_random)
        val ten = BitmapFactory.decodeResource(resources, R.drawable.turntable_packet_ten)
        val thanks = BitmapFactory.decodeResource(resources, R.drawable.turntable_thanks)
        val coins = BitmapFactory.decodeResource(resources, R.drawable.turntable_coins)
        val card = BitmapFactory.decodeResource(resources, R.drawable.turntable_card)
        val lotteries: MutableList<LotterySpanView.Lottery> = ArrayList()
        lotteries.add(LotteryItem("100元话费红包", hundred, Color.parseColor("#FCF2D4")))
        lotteries.add(LotteryItem("1枚金币", coins, Color.parseColor("#FCF7E8")))
        lotteries.add(LotteryItem("10元话费红包", ten, Color.parseColor("#FCF2D4")))
        lotteries.add(LotteryItem("10张抽奖卡", card, Color.parseColor("#FCF7E8")))
        lotteries.add(LotteryItem("谢谢参与", thanks, Color.parseColor("#FCF2D4")))
        lotteries.add(LotteryItem("1张抽奖卡", card, Color.parseColor("#FCF7E8")))
        lotteries.add(LotteryItem("随机话费红包", random, Color.parseColor("#FCF2D4")))
        lotteries.add(LotteryItem("10枚金币", coins, Color.parseColor("#FCF7E8")))
        span?.lotteries = lotteries
    }

}