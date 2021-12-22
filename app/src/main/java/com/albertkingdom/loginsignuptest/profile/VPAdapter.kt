package com.albertkingdom.loginsignuptest.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class VPAdapter(fragment: Fragment, val email: String): FragmentStateAdapter(fragment) {

    var fragments: ArrayList<Fragment> = arrayListOf(
        ProfileSubFragment1(email),
        ProfileSubFragment2(),

    )
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}