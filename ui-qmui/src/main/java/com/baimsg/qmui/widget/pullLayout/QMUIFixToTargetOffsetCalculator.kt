package com.baimsg.qmui.widget.pullLayout

import com.baimsg.qmui.widget.pullLayout.QMUIPullLayout.ActionViewOffsetCalculator
import com.baimsg.qmui.widget.pullLayout.QMUIPullLayout.PullAction

class QMUIFixToTargetOffsetCalculator : ActionViewOffsetCalculator {
    override fun calculateOffset(pullAction: PullAction?, targetOffset: Int): Int {
        return if (targetOffset < pullAction!!.targetTriggerOffset) {
            targetOffset + pullAction.actionInitOffset
        } else pullAction.targetTriggerOffset + pullAction.actionInitOffset
    }
}