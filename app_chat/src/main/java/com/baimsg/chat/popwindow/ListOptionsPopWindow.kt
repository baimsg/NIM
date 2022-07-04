package com.baimsg.chat.popwindow

import android.app.Activity
import android.view.*
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baimsg.chat.R
import com.baimsg.chat.adapter.ListOptionsAdapter
import com.baimsg.chat.databinding.PopListOptionsBinding
import com.baimsg.data.model.ItemListOptions

/**
 * Create by Baimsg on 2022/7/4
 *
 **/
class ListOptionsPopWindow(
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
    val activity: Activity,
    val items: List<ItemListOptions>,
    val onSelect: (ItemListOptions, Int) -> Unit
) {
    private var popWindow: PopupWindow
    private val confirmView by lazy {
        LayoutInflater.from(activity).inflate(R.layout.pop_list_options, null)
    }

    private val binging: PopListOptionsBinding by lazy {
        PopListOptionsBinding.bind(confirmView)
    }

    private val listOptionsAdapter by lazy {
        ListOptionsAdapter()
    }

    init {
        popWindow = PopupWindow(confirmView, width, height)
        /**
         * 允许弹出窗口超出屏幕边界
         */
        popWindow.isClippingEnabled = false

        /**
         * 弹出窗口应该接收外部触摸事件
         */
        popWindow.isOutsideTouchable = true

        /**
         * 获取焦点
         */
        popWindow.isFocusable = true

        binging.ryContent.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = listOptionsAdapter
            listOptionsAdapter.setList(items)
        }

        listOptionsAdapter.setOnItemClickListener { adapter, _, position ->
            onSelect(adapter.data[position] as ItemListOptions, position)
            dismiss()
        }

    }

    fun showAsDropDown(dropDownView: View, x: Int = 100, y: Int = 0) {
        popWindow.showAsDropDown(dropDownView, x, y,  Gravity.START)
    }

    private fun dismiss() {
        popWindow.dismiss()
    }


}