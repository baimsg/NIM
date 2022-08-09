package com.baimsg.chat.fragment.user

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentUserDetailBinding
import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.chat.util.extensions.*
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
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvRetry.setOnClickListener {
            userDetailViewModel.loadData()
        }
    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            userDetailViewModel.observeViewState.collectLatest {
                when (it.executionStatus) {
                    ExecutionStatus.LOADING -> {
                        binding.proLoading.show()
                        binding.groupMain.hide()
                        binding.tvRetry.hide()
                    }
                    ExecutionStatus.SUCCESS -> {
                        binding.proLoading.hide()
                        binding.groupMain.show()
                        val info = it.userInfo
                        binding.tvDeleteFriend.setText(if (it.myFriend) R.string.delete_friend else R.string.add_friend)
                        binding.ivAvatar.loadImage(info.avatar)
                        binding.tvName.text = info.name
                        binding.tvGender.text = info.genderEnum.message()
                        binding.tvAccount.text = "ID:${info.account}"
                        binding.tvSignature.text = info.signature ?: "没有个性签名哦！"
                    }
                    ExecutionStatus.FAIL -> {
                        binding.proLoading.hide()
                        binding.tvRetry.show()
                    }
                    else -> Unit
                }
            }
        }
    }


}