package com.jpz.workoutnotebook.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ViewPagerAdapter
import com.jpz.workoutnotebook.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val EDIT = "EDIT"
    }

    private var fabSelected = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureToolbar()
        configureViewPagerAdapter()
        configureTabLayout()
        animateFAB()
        clickOnFAB()
    }

    //--------------------------------------------------------------------------------------
    // UI

    private fun configureToolbar() {
        // Get the toolbar view inside the activity layout
        setSupportActionBar(toolbar)
    }

    private fun configureViewPagerAdapter() {
        val viewPagerAdapter = ViewPagerAdapter(this)
        mainActivityViewPager.adapter = viewPagerAdapter
    }

    private fun configureTabLayout() {
        TabLayoutMediator(mainActivityTabLayout, mainActivityViewPager) { tab, position ->
            tab.text = "OBJECT ${(position + 1)}"
        }.attach()
    }

    //--------------------------------------------------------------------------------------

    // FAB disappear and appear when a page is scrolled or selected
    private fun animateFAB() {
        mainActivityViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                fabSelected = position
                mainActivityFAB.hide()
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    mainActivityFAB.show()
                }
                if (state == ViewPager2.SCROLL_STATE_DRAGGING || fabSelected == 0) {
                    mainActivityFAB.hide()
                }
            }
        })
    }

    //--------------------------------------------------------------------------------------

    private fun clickOnFAB() {
        mainActivityFAB.setOnClickListener {
            val myFragment =
                supportFragmentManager.findFragmentByTag("f" + mainActivityViewPager.currentItem)
            if (myFragment is ProfileFragment) {
                val intent = Intent(this, EditActivity::class.java)
                intent.putExtra(EDIT, ProfileFragment::class.java.name)
                startActivity(intent)
            }
        }
    }

}