package com.baimsg.chat.fragment.search

import android.Manifest
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.baimsg.chat.Constant
import com.baimsg.chat.R
import com.baimsg.chat.adapter.AddFriendAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentSearchUserBinding
import com.baimsg.chat.type.BatchStatus
import com.baimsg.chat.util.extensions.*
import com.chad.library.adapter.base.animation.AlphaInAnimation
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.flow.collectLatest
import java.io.File


class SearchUserFragment : BaseFragment<FragmentSearchUserBinding>(R.layout.fragment_search_user) {

    private val searchUserViewModel by lazy {
        ViewModelProvider(requireActivity())[SearchUserViewModel::class.java]
    }

    private val friendAdapter by lazy {
        AddFriendAdapter()
    }

    override fun initView() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ivEdit.setOnClickListener {
            findNavController().navigate(R.id.action_searchUserFragment_to_settingFragment)
        }

        binding.ivClean.setOnClickListener {
            searchUserViewModel.stopSearchUser()
        }

        binding.ivSave.setOnClickListener {
            PermissionX.init(this).permissions(
                listOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        val path =
                            requireContext().getExternalFilesDir("${""}-${""}")
                        if (path == null) {
                            showError("获取不到外置储存卡")
                            return@request
                        }
                        path.apply {
                            try {
                                val file = File(this.absolutePath + File.separator + "log.ini")
                                val sb = StringBuilder()
                                searchUserViewModel.searchViewState.value.allUser.forEachIndexed { index, nimUserInfo ->
                                    if (index > 0) sb.append("\n")
                                    sb.append("序号：${index + 1}\t${nimUserInfo.name}\tid：${nimUserInfo.account}")
                                }
                                file.writeText(sb.toString())
                                MaterialDialog(requireContext()).show {
                                    title(text = "导出成功")
                                    message(text = "路径：${file.absolutePath}")
                                    positiveButton(text = "知道了") { }
                                    negativeButton(text = "复制地址") {
                                        showSuccess("复制成功")
                                        requireContext().copy(file.absolutePath)
                                    }
                                }
                            } catch (e: Exception) {
                                showError("保存数据失败")

                            }
                        }
                    } else {
                        showError("部分权限未获得")
                    }
                }
        }

        binding.ivStart.setOnClickListener {
            searchUserViewModel.searchUser()
        }

        binding.fabAdd.setOnClickListener {
            searchUserViewModel.addFriend()
        }

        binding.srContent.apply {
            setColorSchemeResources(R.color.color_primary)
            setOnRefreshListener {
                friendAdapter.setList(searchUserViewModel.searchViewState.value.allUser.filter { it.loaded })
                isRefreshing = false
            }
        }

        binding.ryContent.apply {
            friendAdapter.animationEnable = true
            friendAdapter.adapterAnimation = AlphaInAnimation()

            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = friendAdapter
        }

        binding.editAccount.apply {
            showKeyboard(true)
            addTextChangedListener {
                try {
                    val account = if (it.isNullOrBlank()) 0 else it.toString().toLong()
                    searchUserViewModel.updateAccount(account)
                } catch (e: Exception) {
                    showError("请检查您输入的内容")
                }
            }
        }

    }

    override fun initLiveData() {
        repeatOnLifecycleStarted {
            searchUserViewModel.addFriendViewState.collectLatest {
                binding.tvProgress.text = "${it.user.account}-${it.message}"
            }
        }

        repeatOnLifecycleStarted {
            searchUserViewModel.searchViewState.collectLatest { value ->
                value.apply {
                    binding.tvCount.text = "(${allUser.size})"
                    binding.tvProgress.text = "(${count}/${Constant.SEARCH_COUNT})"
                    when (status) {
                        BatchStatus.RUNNING -> {
                            binding.ivStart.setImageResource(R.drawable.ic_pause)
                            binding.ivBack.hide()
                            binding.ivEdit.hide()
                            binding.ivSave.hide()
                            binding.ivClean.hide()
                            binding.fabAdd.hide()
                            binding.proLoading.show()
                            binding.editAccount.isEnabled = false
                            if (update) friendAdapter.addData(users)
                            binding.editAccount.setText("${value.account}")
                        }
                        BatchStatus.STOP -> {
                            binding.fabAdd.hide()
                            binding.ivSave.hide()
                            binding.ivClean.hide()
                            friendAdapter.setList(null)
                            binding.editAccount.isEnabled = true
                        }
                        else -> {
                            binding.proLoading.hide()
                            binding.ivBack.show()
                            binding.ivEdit.show()
                            if (allUser.isNotEmpty()) {
                                binding.ivSave.show()
                                binding.ivClean.show()
                                binding.fabAdd.show()
                            }
                            binding.ivStart.setImageResource(R.drawable.ic_start)
                        }
                    }
                }

            }
        }

    }

    override fun onPause() {
        super.onPause()
        binding.ivBack.hideKeyboard()
    }
}