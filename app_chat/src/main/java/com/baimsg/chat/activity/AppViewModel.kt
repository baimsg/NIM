package com.baimsg.chat.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.data.api.BaseEndpoints
import com.baimsg.data.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Create by Baimsg on 2022/7/19
 *
 **/
@HiltViewModel
class AppViewModel @Inject constructor(
    private val baseEndpoints: BaseEndpoints
) : ViewModel() {

    private val _config: MutableStateFlow<Async<ConfigBean>> by lazy {
        MutableStateFlow(Uninitialized)
    }

    val observeConfig: StateFlow<Async<ConfigBean>> = _config

    val users: List<User>
        get() = _config.value.invoke()?.users ?: emptyList()

    val noticeVersion: Int
        get() = _config.value.invoke()?.noticeVersion ?: 0

    val noticeTitle: String
        get() = _config.value.invoke()?.noticeTitle ?: ""

    val noticeContent: String
        get() = _config.value.invoke()?.noticeContent ?: ""

    val stopUsing: Boolean
        get() = _config.value.invoke()?.stopUsing ?: false

    val noticeLink: String
        get() = _config.value.invoke()?.noticeLink ?: ""

    init {
        initBaseConfig()
    }

    fun retry() {
        initBaseConfig()
    }

    /**
     * 获取云端数据
     */
    private fun initBaseConfig() {
        viewModelScope.launch {
            _config.apply {
                value = Loading()
                value = try {
                    Success(
                        baseEndpoints.getPersonal(
                            fileId = "f0bc4b8ad2bb6ce1ace2e3d0bf48d703",
                            shareKey = "f07c493ac7be7334d45a32fb8509fe98"
                        )
                    )
                } catch (e: Exception) {
                    Fail(e)
                }
            }
        }
    }


}