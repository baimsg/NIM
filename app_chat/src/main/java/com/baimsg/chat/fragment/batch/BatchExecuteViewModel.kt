package com.baimsg.chat.fragment.batch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.data.db.daos.TaskAccountDao
import com.baimsg.data.model.entities.NIMTaskAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BatchExecuteViewModel @Inject constructor(
    private val taskAccountDao: TaskAccountDao
) : ViewModel() {

    private val _taskAccount by lazy {
        MutableStateFlow(emptyList<NIMTaskAccount>())
    }

    val observeTaskAccount: StateFlow<List<NIMTaskAccount>> = _taskAccount

    val taskAccount: List<NIMTaskAccount>
        get() = _taskAccount.value

    fun loadAllAccount(appKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _taskAccount.value = taskAccountDao.entriesByAppKey(appKey = appKey)
        }
    }

}