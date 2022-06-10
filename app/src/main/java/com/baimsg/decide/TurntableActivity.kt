package com.baimsg.decide

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.baimsg.decide.turntableview.ITurntableListener
import com.baimsg.decide.turntableview.TurntableView

/**
 * Create by Baimsg on 2022/6/10
 *
 **/
class TurntableActivity : AppCompatActivity() {
    private lateinit var mTurntable: TurntableView
    private lateinit var mIvGo: ImageView
    private lateinit var mBtChangeColor: Button
    private lateinit var mBtChangeData: Button
    private lateinit var mTvResult: TextView
    private lateinit var mBtPointTo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_turntable)
        mTurntable = findViewById(R.id.turntable)
        mIvGo = findViewById(R.id.iv_node)
        mBtChangeColor = findViewById(R.id.bt_changecolor)
        mBtChangeData = findViewById(R.id.bt_changedata)
        mTvResult = findViewById(R.id.tv_result)
        mBtPointTo = findViewById(R.id.bt_point_to)
        mBtPointTo.setOnClickListener {
            mTurntable.startRotate(7, object : ITurntableListener {
                override fun onStart() {
                    Toast.makeText(this@TurntableActivity, "开始抽奖", Toast.LENGTH_SHORT).show()
                }

                override fun onEnd(position: Int, name: String) {
                    mTvResult.text = "抽奖结束抽中第" + (position + 1) + "位置的奖品:" + name
                }
            })
        }
        mBtChangeColor.setOnClickListener { //设置item的颜色
            changeColors()
        }
        mBtChangeData.setOnClickListener { //修改转盘数据
            changeDatas()
        }

        //开始抽奖
        mIvGo.setOnClickListener {
            mTurntable.startRotate(object : ITurntableListener {
                override fun onStart() {
                    Toast.makeText(this@TurntableActivity, "开始抽奖", Toast.LENGTH_SHORT).show()
                }

                override fun onEnd(position: Int, name: String) {
                    mTvResult.text = "抽奖结束抽中第" + (position + 1) + "位置的奖品:" + name
                }
            })
        }
    }

    private fun changeColors() {
        val colors = ArrayList<Int>()
        colors.add(Color.parseColor("#ff8584"))
        colors.add(resources.getColor(R.color.amber_500))
        colors.add(Color.parseColor("#000000"))
        mTurntable.setBackColor(colors)
    }

    private fun changeDatas() {
        val num = 5
        val names = ArrayList<String>()
        val bitmaps = ArrayList<Bitmap>()
        for (i in 0 until num) {
            names.add("第" + (i + 1))
            bitmaps.add(BitmapFactory.decodeResource(resources, R.drawable.sports))
        }
        mTurntable.setDatas(num, names, bitmaps)
    }
}