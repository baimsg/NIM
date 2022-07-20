package com.baimsg.chat.fragment.team

import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.data.model.Async
import com.baimsg.data.model.Uninitialized
import com.baimsg.data.model.entities.NIMTeam


data class TeamViewState(
    val teams: Async<List<NIMTeam>>
) {
    companion object {
        val EMPTY = TeamViewState(Uninitialized)
    }
}

data class CreateTeamState(
    val executionStatus: ExecutionStatus,
    val name: String,
    val message: String,
) {
    companion object {
        val EMPTY = CreateTeamState(ExecutionStatus.UNKNOWN, name = "", message = "")
    }
}