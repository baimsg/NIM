package com.baimsg.chat.fragment.user

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentUserDetailBinding
import com.baimsg.chat.fragment.login.LoginViewModel
import com.baimsg.chat.util.extensions.loadImage
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.data.model.Fail
import com.baimsg.data.model.Loading
import com.baimsg.data.model.Success
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * Create by Baimsg on 2022/8/7
 *
 **/
@AndroidEntryPoint
class UserDetailFragment : BaseFragment<FragmentUserDetailBinding>(R.layout.fragment_user_detail) {

    private val userDetailViewModel by viewModels<UserDetailViewModel>()

    private val loginViewModel by activityViewModels<LoginViewModel>()

    override fun initView() {
        userDetailViewModel.initUserInfo.apply {
            binding.ivAvatar.loadImage(avatar)
            binding.tvName.text = name
            binding.tvAccount.text = "ID:$account"
        }

        binding.editData.apply {
            keyListener = null
        }
    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            userDetailViewModel.observeViewState.collectLatest {
                when (it.data) {
                    is Success -> {
                        binding.editData.setText(it.data.invoke())
                    }
                    is Loading -> {
                        binding.editData.setText("正在加载ing")
                    }
                    is Fail -> {
                        binding.editData.setText(it.data.error.message)
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun initData() {
        userDetailViewModel.getUserInfo("http://yuchenwangluo.xyz/", loginViewModel.currentAppKey)
    }

}