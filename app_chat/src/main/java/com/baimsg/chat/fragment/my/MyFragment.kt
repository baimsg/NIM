package com.baimsg.chat.fragment.my

import androidx.navigation.fragment.findNavController
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentMyBinding

/**
 * Create by Baimsg on 2022/6/14
 *
 **/
class MyFragment : BaseFragment<FragmentMyBinding>(R.layout.fragment_my) {

    override fun initView() {
        binding.ivSetting.setOnClickListener {
            findNavController().navigate(R.id.action_myFragment_to_settingFragment)
        }
    }
}