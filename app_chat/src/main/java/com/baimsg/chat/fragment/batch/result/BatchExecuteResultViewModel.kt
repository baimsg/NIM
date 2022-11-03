package com.baimsg.chat.fragment.batch.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.baimsg.chat.fragment.batch.TaskResult
import com.baimsg.data.model.JSON
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject

/**
 * Create by Baimsg on 2022/11/3
 *
 **/
class BatchExecuteResultViewModel @Inject constructor(
    handle: SavedStateHandle,
) : ViewModel() {

    private val taskResult = handle["taskResult"] ?: ""

    private val _taskResults by lazy {
        MutableStateFlow(listOf<TaskResult>())
    }

    val observeTaskResult: StateFlow<List<TaskResult>> = _taskResults

    val taskResults: List<TaskResult>
        get() = _taskResults.value

    init {
        loadData()
    }

    fun loadData() {
        if (taskResult.isNotBlank()) {
            _taskResults.value =
                JSON.decodeFromString(ListSerializer(TaskResult.serializer()), taskResult)
        }
    }
}