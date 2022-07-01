package com.baimsg.chat.fragment.batch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baimsg.chat.type.UpdateStatus
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

    private val _viewState by lazy {
        MutableStateFlow(BatchExecuteViewState.EMPTY)
    }

    val observeViewState: StateFlow<BatchExecuteViewState> = _viewState

    private val allTaskAccounts: List<NIMTaskAccount>
        get() = _viewState.value.allTaskAccounts

    fun loadAllAccount(appKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.apply {
                value = value.copy(
                    allTaskAccounts = taskAccountDao.entriesByAppKey(appKey),
                    updateStatus = UpdateStatus.REFRESH
                )
            }
        }
    }

    fun deleteById(nimTaskAccount: NIMTaskAccount) {
        viewModelScope.launch(Dispatchers.IO) {
            taskAccountDao.deleteById(nimTaskAccount.id)
            _viewState.apply {
                value = value.copy(allTaskAccounts = allTaskAccounts.toMutableList().apply {
                    remove(nimTaskAccount)
                }, updateStatus = UpdateStatus.DEFAULT)
            }
        }
    }

}