package com.baimsg.chat.activity

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseActivity
import com.baimsg.chat.databinding.ActivityMainBinding
import com.baimsg.chat.fragment.login.LoginAction
import com.baimsg.chat.fragment.login.LoginViewModel
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.setStatusBarDarkMode
import com.baimsg.chat.util.extensions.setStatusBarLightMode
import com.baimsg.chat.util.extensions.showShort
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.SDKOptions
import com.netease.nimlib.sdk.auth.AuthServiceObserver
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.lifecycle.SdkLifecycleObserver
import com.netease.nimlib.sdk.util.NIMUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/**
 * Create by Baimsg on 2022/6/10
 *
 **/
@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val loginViewModel by viewModels<LoginViewModel>()

    override fun initView() {
        /**
         * 初始化云信
         */
        NIMClient.initSDK()

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                setStatusBarLightMode()
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                setStatusBarDarkMode()
            }
        }

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
        //监听注册状态
        NIMClient.getService(SdkLifecycleObserver::class.java)
            .observeMainProcessInitCompleteResult({
                if (!it) showShort("SDK初始化失败!")
            }, true)

        //监听登录状态
        NIMClient.getService(AuthServiceObserver::class.java).observeOnlineStatus({ status ->
            repeatOnLifecycleStarted {
                loginViewModel.submit(LoginAction.UpdateStatusCode(status))
            }
        }, true)

    }

    override fun onBackPressed() {
        if (!onBackPressedDispatcher.hasEnabledCallbacks()) {
            super.onBackPressed()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}