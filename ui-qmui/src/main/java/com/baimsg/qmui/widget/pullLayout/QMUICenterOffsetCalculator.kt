package com.baimsg.qmui.widget.pullLayout

import com.baimsg.qmui.widget.pullLayout.QMUIPullLayout.ActionViewOffsetCalculator
import com.baimsg.qmui.widget.pullLayout.QMUIPullLayout.PullAction

class QMUICenterOffsetCalculator : ActionViewOffsetCalculator {
    override fun calculateOffset(pullAction: PullAction?, targetOffset: Int): Int {
        return if (targetOffset < pullAction!!.targetTriggerOffset) {
            targetOffset + pullAction.actionInitOffset
        } else (targetOffset - pullAction.actionPullSize) / 2
    }
}