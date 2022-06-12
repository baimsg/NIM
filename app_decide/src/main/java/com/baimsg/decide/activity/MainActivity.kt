package com.baimsg.decide.activity

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.baimsg.decide.R
import com.baimsg.decide.base.BaseActivity
import com.baimsg.decide.databinding.ActivityMainBinding

/**
 * Create by Baimsg on 2022/6/10
 *
 **/
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun initView() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        if (!onBackPressedDispatcher.hasEnabledCallbacks()) {
            super.onBackPressed()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}