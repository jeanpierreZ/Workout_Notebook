package com.jpz.workoutnotebook.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jpz.workoutnotebook.fragments.ProfileFragment
import com.jpz.workoutnotebook.fragments.SportsFragment

class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    companion object {
        // Field for the number of page to show
        private const val NUM_ITEMS = 5
    }

    override fun getItemCount(): Int = NUM_ITEMS

    override fun createFragment(position: Int): Fragment {
        var fragment = Fragment()

        // Fragment to return
        when (position) {
            0 -> fragment = SportsFragment()
            1 -> fragment = SportsFragment()
            2 -> fragment = SportsFragment()
            3 -> fragment = SportsFragment()
            4 -> fragment = ProfileFragment()
        }
        return fragment
    }
}