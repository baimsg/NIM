package com.baimsg.chat.fragment.search

import androidx.lifecycle.ViewModelProvider
import com.baimsg.chat.R
import com.baimsg.chat.adapter.AccountMediumAdapter
import com.baimsg.chat.base.BaseFragment
import com.baimsg.chat.databinding.FragmentSearchBinding


class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {

    private val searchViewModel by lazy {
        ViewModelProvider(this)[SearchViewModel::class.java]
    }

    private val accountMediumAdapter by lazy {
        AccountMediumAdapter()
    }

    override fun initView() {

    }
}