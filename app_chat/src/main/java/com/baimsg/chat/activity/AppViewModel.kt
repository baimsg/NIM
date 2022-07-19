package com.baimsg.chat.activity

import androidx.lifecycle.ViewModel
import com.baimsg.data.api.BaseEndpoints
import com.baimsg.data.model.Async
import com.baimsg.data.model.BaseConfig
import com.baimsg.data.model.Fail
import com.baimsg.data.model.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Create by Baimsg on 2022/7/19
 *
 **/
@HiltViewModel
class AppViewModel @Inject constructor(
    private val baseEndpoints: BaseEndpoints
) : ViewModel() {


    /**
     * 获取云端数据
     */
    suspend fun getBaseConfig(): Async<BaseConfig> =
        withContext(Dispatchers.IO) {
            try {
                Success(baseEndpoints.getKey())
            } catch (e: Exception) {
                Fail(e)
            }
        }
}