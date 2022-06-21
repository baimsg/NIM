package com.baimsg.chat.fragment.setting.appkey

import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.baimsg.base.util.KvUtils
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentAppKeyBinding
import com.baimsg.chat.util.extensions.hide
import com.baimsg.chat.util.extensions.show
import com.baimsg.chat.util.extensions.showKeyboard
import com.baimsg.chat.util.extensions.showShort

/**
 * Create by Baimsg on 2022/6/14
 *
 **/
class AppKeyFragment : BaseFragment<FragmentAppKeyBinding>(R.layout.fragment_app_key) {

    private var appKey: String = ""

    override fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.editAppKey.apply {
            showKeyboard(false)
            addTextChangedListener {
                appKey = it.toString()
                if (it.isNullOrBlank()) binding.tvFinish.hide() else binding.tvFinish.show()
            }
        }

        binding.tvFinish.setOnClickListener {
            KvUtils.put(Constant.KEY_APP_KEY, appKey)
            showShort("设置成功")
            findNavController().navigateUp()
        }

    }
}