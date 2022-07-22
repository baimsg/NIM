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

    private val _baseConfig: MutableStateFlow<Async<BaseConfig>> by lazy {
        MutableStateFlow(Uninitialized)
    }

    val observeBaseConfig: StateFlow<Async<BaseConfig>> = _baseConfig

    val verifyKey: String
        get() = _baseConfig.value.invoke()?.id ?: ""

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
            _baseConfig.apply {
                value = Loading()
                value = try {
                    Success(baseEndpoints.getKey())
                } catch (e: Exception) {
                    Fail(e)
                }
            }
        }
    }
}