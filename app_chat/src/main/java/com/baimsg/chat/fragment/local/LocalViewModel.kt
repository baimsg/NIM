package com.baimsg.chat.fragment.local

import androidx.lifecycle.ViewModel
import com.baimsg.data.db.daos.UserInfoDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    suspend fun loadAllAccount(appKey: String) =
        withContext(Dispatchers.IO) {
            userInfoDao.entriesByAppKey(appKey = appKey)
        }
}