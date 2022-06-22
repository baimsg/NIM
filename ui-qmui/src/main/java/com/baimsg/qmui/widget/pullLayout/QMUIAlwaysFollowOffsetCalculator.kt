package com.baimsg.qmui.widget.pullLayout

import com.baimsg.qmui.widget.pullLayout.QMUIPullLayout.ActionViewOffsetCalculator
import com.baimsg.qmui.widget.pullLayout.QMUIPullLayout.PullAction

class QMUIAlwaysFollowOffsetCalculator : ActionViewOffsetCalculator {
    override fun calculateOffset(pullAction: PullAction?, targetOffset: Int): Int {
        return targetOffset + pullAction!!.actionInitOffset
    }
}