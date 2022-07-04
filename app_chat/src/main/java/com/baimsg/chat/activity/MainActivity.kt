package com.baimsg.chat.activity

import android.content.res.Configuration
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseActivity
import com.baimsg.chat.databinding.ActivityMainBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import com.baimsg.chat.util.extensions.*
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.auth.AuthServiceObserver
import com.netease.nimlib.sdk.lifecycle.SdkLifecycleObserver
import dagger.hilt.android.AndroidEntryPoint

/**
 * Create by Baimsg on 2022/6/10
 *
 **/
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val loginViewModel by viewModels<LoginViewModel>()

    private val authServiceObserver by lazy {
        NIMClient.getService(AuthServiceObserver::class.java)
    }

    private val sdkLifecycleObserver by lazy {
        NIMClient.getService(SdkLifecycleObserver::class.java)
    }

    override fun initView() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                setStatusBarLightMode()
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                setStatusBarDarkMode()
            }
        }

        /**
         * 初始化云信
         */
        NIMClient.initSDK()

        /**
         * 监听初始化状态
         */
        sdkLifecycleObserver.observeMainProcessInitCompleteResult({
            if (!it) showError("IM Failed to load")
        }, true)

        //监听登录状态
        authServiceObserver.observeOnlineStatus({ status ->
            repeatOnLifecycleStarted {
                loginViewModel.updateStatusCode(status)
            }
        }, true)

        //绑定bottomNavigation
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController)
        //监听导航事件
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.myFragment -> binding.bottomNav.visibility = View.VISIBLE
                R.id.homeFragment -> binding.bottomNav.visibility = View.VISIBLE
                else -> binding.bottomNav.visibility = View.GONE
            }
        }
    }


    override fun onBackPressed() {
        if (!onBackPressedDispatcher.hasEnabledCallbacks()) {
            super.onBackPressed()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}