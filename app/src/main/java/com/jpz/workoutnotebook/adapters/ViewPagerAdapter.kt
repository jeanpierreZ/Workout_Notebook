package com.jpz.workoutnotebook.adapters

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.jpz.workoutnotebook.fragments.ProfileFragment

class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    companion object {
        // Field for the number of page to show
        private const val NUM_ITEMS = 5
        const val ARG_OBJECT = "object"
    }

    override fun getItemCount(): Int = NUM_ITEMS

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val fragment = ProfileFragment()
        fragment.arguments = Bundle().apply {
            // Our object is just an integer :-P
            putInt(ARG_OBJECT, position + 1)
        }
        return fragment
    }

}