package com.baimsg.chat.fragment.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.data.db.daos.UserInfoDao
import com.baimsg.data.model.entities.NIMUserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
@HiltViewModel
class LocalViewModel @Inject constructor(
    private val userInfoDao: UserInfoDao
) : ViewModel() {

    private val _allAccount by lazy {
        MutableStateFlow(emptyList<NIMUserInfo>())
    }

    val observeAllAccount: StateFlow<List<NIMUserInfo>> = _allAccount

    val allAccount: List<NIMUserInfo>
        get() = _allAccount.value

    fun loadAllAccount(appKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _allAccount.value = userInfoDao.entriesByAppKey(appKey = appKey)
        }
    }
}