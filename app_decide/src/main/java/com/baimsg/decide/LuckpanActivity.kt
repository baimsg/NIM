package com.baimsg.decide

import android.os.Bundle
import android.transition.TransitionManager.go
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.baimsg.decide.luckpan.LuckPanLayout
import com.baimsg.decide.luckpan.RotatePan

/**
 * Create by Baimsg on 2022/6/10
 *
 **/
class LuckpanActivity : AppCompatActivity(), LuckPanLayout.AnimationEndListener {
    private val rotatePan: RotatePan? = null
    private var luckPanLayout: LuckPanLayout? = null
    private var goBtn: ImageView? = null
    private var yunIv: ImageView? = null
    private lateinit var strs: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_luckpan)
        strs = resources.getStringArray(R.array.names)
        luckPanLayout = findViewById(R.id.luckpan_layout)
        luckPanLayout?.animationEndListener = this
        goBtn = findViewById(R.id.go)
        yunIv = findViewById(R.id.yun)
    }

    fun rotation(view: View?) {
        luckPanLayout?.rotate(-1, 100)
    }

    override fun endAnimation(position: Int) {
        Toast.makeText(this, "Position = " + position + "," + strs[position], Toast.LENGTH_SHORT)
            .show()
    }

}