package com.isfandroid.pomodaily.presentation.common.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerFragmentsAdapter(
    private val fragments: List<Fragment>,
    fragment: Fragment
): FragmentStateAdapter(fragment) {

    override fun getItemCount() = fragments.size
    override fun createFragment(position: Int) = fragments[position]
}