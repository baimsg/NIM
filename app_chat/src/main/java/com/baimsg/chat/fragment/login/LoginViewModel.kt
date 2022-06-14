package com.baimsg.chat.fragment.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.bean.NIMUserInfo
import com.netease.nimlib.sdk.StatusCode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Create by Baimsg on 2022/6/14
 *
 **/
internal class LoginViewModel constructor(application: Application) :
    AndroidViewModel(application) {

    val statusCode by lazy {
        MutableStateFlow(StatusCode.INVALID)
    }

    val userInfo by lazy {
        MutableStateFlow(NIMUserInfo())
    }


    private val pending by lazy {
        MutableSharedFlow<LoginAction>()
    }

    init {
        viewModelScope.launch {
            pending.collectLatest { action ->
                when (action) {
                    is LoginAction.SetStatusCode -> {
                        statusCode.value = action.statusCode
                    }
                }
            }
        }
    }

    fun submit(action: LoginAction) {
        viewModelScope.launch {
            pending.emit(action)
        }
    }


}