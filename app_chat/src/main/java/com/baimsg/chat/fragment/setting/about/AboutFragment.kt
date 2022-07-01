package com.baimsg.chat.fragment.setting.about

import androidx.navigation.fragment.findNavController
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentAboutBinding
import com.baimsg.chat.util.PackageHelper

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
class AboutFragment : BaseFragment<FragmentAboutBinding>(R.layout.fragment_about) {

    override fun initView() {

        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvVersion.text = PackageHelper.getAppVersion(requireContext())
    }
}