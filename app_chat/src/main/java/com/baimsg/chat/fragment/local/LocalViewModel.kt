package com.baimsg.chat.fragment.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.util.verifySensitiveWords
import com.baimsg.data.db.daos.TaskAccountDao
import com.baimsg.data.db.daos.UserInfoDao
import com.baimsg.data.model.entities.NIMUserInfo
import com.baimsg.data.model.entities.asTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Create by Baimsg on 2022/7/1
 *
 **/
@HiltViewModel
class LocalViewModel @Inject constructor(
    private val userInfoDao: UserInfoDao,
    private val taskAccountDao: TaskAccountDao
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

    /**
     * 全部加入任务
     */
    fun addTaskAll() {
        viewModelScope.launch(Dispatchers.IO) {
            val tasks = allAccount.filter { !it.name.verifySensitiveWords() }.map { it.asTask() }
            taskAccountDao.updateOrInsert(tasks)
        }
    }


    /**
     * 添加到任务
     * @param nimUserInfo
     */
    fun addTask(nimUserInfo: NIMUserInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            taskAccountDao.updateOrInsert(nimUserInfo.asTask())
        }
    }

    /**
     *
     */
    fun deleteAccountById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userInfoDao.deleteById(id)
        }
    }

    /**
     *
     */
    fun deleteAll(appKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userInfoDao.deleteByAppKey(appKey)
        }
    }
}