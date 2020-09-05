package com.jpz.workoutnotebook.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPagerAdapter = ViewPagerAdapter(this)
        mainActivityViewPager.adapter = viewPagerAdapter

        TabLayoutMediator(mainActivityTabLayout, mainActivityViewPager) { tab, position ->
            tab.text = "OBJECT ${(position + 1)}"
        }.attach()
    }

}