package com.baimsg.decide

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.baimsg.decide.view.RotateLayoutView
import com.baimsg.decide.view.RotateView
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var mRotateView: RotateView
    private lateinit var mRotateView2: RotateView

    var images: MutableList<Int> = ArrayList()
    var names: MutableList<String> = ArrayList()
    private lateinit var mRotateLayoutView: RotateLayoutView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        intDate()
        initRotate1()
        initRotate2()
        initRotate3()
    }


    //不同版本的View
    private fun initRotate1() {
        mRotateView = findViewById(R.id.rv_rotateview)
        mRotateView.setImageIcon(images)
        mRotateView.setStrName(names)
        findViewById<ImageView>(R.id.iv_start).setOnClickListener {
            //-1为随机数或者指定位置，但必须小于总个数
            mRotateView.startAnimation(-1)
        }

        //获取到位置
        mRotateView.setOnCallBackPosition { pos ->
            Toast.makeText(
                this@MainActivity,
                "位置：" + names[pos],
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //不同版本的View
    private fun initRotate2() {
        mRotateView2 = findViewById(R.id.rv_rotateview2)
        mRotateView2.setImageIcon(images)
        mRotateView2.setStrName(names)
        findViewById<ImageView>(R.id.iv_start2).setOnClickListener(View.OnClickListener { //-1为随机数或者指定位置，但必须小于总个数
            mRotateView2.startAnimation(-1)
        })
        //获取到位置
        mRotateView2.setOnCallBackPosition { pos ->
            Toast.makeText(
                this@MainActivity,
                "位置：" + names[pos],
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //将两个自定义View变成一个了，方法上有点变化而已，其他的没有太大的变化
    private fun initRotate3() {
        mRotateLayoutView = findViewById(R.id.rv_rotatelayoutview)
        mRotateLayoutView.setImageIcon(images)
        mRotateLayoutView.setStrName(names)
        mRotateLayoutView.setOnCallBackPosition(-1) { pos ->
            Toast.makeText(this@MainActivity, "位置：" + names[pos], Toast.LENGTH_SHORT).show()
        }
    }

    private fun intDate() {
       for (i in  1..40){
           images.add(R.drawable.sports)
        }
        images.forEachIndexed { index, _ ->
            names.add("$index")
        }
    }
}