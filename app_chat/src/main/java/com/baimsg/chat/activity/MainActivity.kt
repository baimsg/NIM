package com.baimsg.chat.activity

import android.view.View
import androidx.lifecycle.*
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseActivity
import com.baimsg.chat.databinding.ActivityMainBinding
import com.baimsg.chat.fragment.login.LoginAction
import com.baimsg.chat.fragment.login.LoginViewModel
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.showShort
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.StatusCode
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.AuthServiceObserver
import com.netease.nimlib.sdk.lifecycle.SdkLifecycleObserver
import kotlinx.coroutines.launch

/**
 * Create by Baimsg on 2022/6/10
 *
 **/
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val loginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }

    override fun initView() {
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
                loginViewModel.submit(LoginAction.SetStatusCode(status))
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