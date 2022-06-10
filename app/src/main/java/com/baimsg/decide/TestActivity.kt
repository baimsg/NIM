package com.baimsg.decide

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.baimsg.decide.view.TurntableItem
import com.baimsg.decide.view.TurntableView

/**
 * Create by Baimsg on 2022/6/10
 *
 **/
class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val turntableView = findViewById<TurntableView>(R.id.turntableView)

        turntableView.setOnChangeListener(object : TurntableView.OnChangeListener {})
        turntableView.setTurntableItems(
            mutableListOf<TurntableItem>().apply {
                (1..5).forEach {
                    add(TurntableItem("数据$it"))
                }
            }
        )

        findViewById<ImageView>(R.id.iv_indicator).setOnClickListener {
            turntableView.startRotateAnimator()
        }


    }
}