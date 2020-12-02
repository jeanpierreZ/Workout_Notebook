package com.jpz.workoutnotebook.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jpz.workoutnotebook.fragments.mainactivity.*

class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    companion object {
        // Field for the number of page to show
        private const val NUM_ITEMS = 5
    }

    override fun getItemCount(): Int = NUM_ITEMS

    override fun createFragment(position: Int): Fragment {
        // Fragment to return
        return when (position) {
            0 -> SportsFragment()
            1 -> CalendarFragment()
            2 -> StatisticsFragment()
            3 -> CommunityFragment()
            4 -> ProfileFragment()
            else -> SportsFragment()
        }
    }
}