package com.baimsg.chat.fragment.setting.appkey

import androidx.navigation.fragment.findNavController
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentAppkeyBinding

/**
 * Create by Baimsg on 2022/6/14
 *
 **/
class AppKeyFragment : BaseFragment<FragmentAppkeyBinding>(R.layout.fragment_appkey) {
    override fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}