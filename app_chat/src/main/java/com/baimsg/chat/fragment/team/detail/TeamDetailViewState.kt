package com.baimsg.chat.fragment.team.detail

import com.baimsg.chat.type.ExecutionStatus
import com.baimsg.data.model.entities.NIMTeam

/**
 * Create by Baimsg on 2022/7/22
 *
 **/
data class TeamDetailViewState(
    val info: NIMTeam
) {

}

data class DismissTeamViewState(
    val info: NIMTeam,
    val message: String,
    val executionStatus: ExecutionStatus,
) {
    companion object {
        val EMPTY =
            DismissTeamViewState(
                info = NIMTeam(),
                executionStatus = ExecutionStatus.UNKNOWN,
                message = ""
            )
    }
}