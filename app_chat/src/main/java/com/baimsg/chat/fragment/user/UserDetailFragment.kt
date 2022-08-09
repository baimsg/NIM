package com.baimsg.chat.fragment.user

import androidx.fragment.app.viewModels
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentUserDetailBinding
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.util.extensions.loadImage
import com.baimsg.chat.util.extensions.message
import com.baimsg.chat.util.extensions.repeatOnLifecycleStarted
import com.baimsg.chat.util.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

/**
 * Create by Baimsg on 2022/8/7
 *
 **/
@AndroidEntryPoint
class UserDetailFragment : BaseFragment<FragmentUserDetailBinding>(R.layout.fragment_user_detail) {

    private val userDetailViewModel by viewModels<UserDetailViewModel>()

    override fun initView() {

    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            userDetailViewModel.observeViewState.collectLatest {
                binding.proLoading.show(it.loading)
                when (it.executionStatus) {
                    ExecutionStatus.SUCCESS -> {
                        val info = it.userInfo
                        binding.ivAvatar.loadImage(info.avatar)
                        binding.tvName.text = info.name
                        binding.tvGender.text = info.genderEnum.message()
                        binding.tvAccount.text = "ID:${info.account}"
                        binding.tvSignature.text = info.signature ?: "没有个性签名哦！"
                    }
                    ExecutionStatus.FAIL -> {

                    }
                    else -> Unit
                }
            }
        }
    }


}