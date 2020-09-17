package com.jpz.workoutnotebook.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.viewpager2.widget.ViewPager2
import com.firebase.ui.auth.AuthUI
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ViewPagerAdapter
import com.jpz.workoutnotebook.fragments.ProfileFragment
import com.jpz.workoutnotebook.utils.MyUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    enum class Tabs(val position: Int) {
        SPORTS(0),
        CALENDAR(1),
        STATISTICS(2),
        COMMUNITY(3),
        PROFILE(4)
    }

    companion object {
        const val EDIT = "EDIT"
        private const val RC_EDIT_PROFILE: Int = 200
    }

    private var pageSelected = 0
    private val myUtils: MyUtils by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureToolbar()
        configureViewPagerAdapter()
        configureTabLayout()
        animateFAB()
        mainActivityFABEditProfile.setOnClickListener {
            editProfile()
        }
        mainActivityFABDisconnect.setOnClickListener {
            disconnectCurrentUser()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_EDIT_PROFILE) {
            when (resultCode) {
                RESULT_OK -> myUtils.showSnackBar(
                    mainActivityCoordinatorLayout, R.string.user_data_updated
                )
                RESULT_CANCELED -> myUtils.showSnackBar(
                    mainActivityCoordinatorLayout, R.string.update_data_cancel
                )
            }
        }
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
            // Add icons to tabs
            when (position) {
                Tabs.SPORTS.position -> tab.setIcon(R.drawable.ic_baseline_fitness_center_24)
                Tabs.CALENDAR.position -> tab.setIcon(R.drawable.ic_baseline_event_24)
                Tabs.STATISTICS.position -> tab.setIcon(R.drawable.ic_baseline_timeline_24)
                Tabs.COMMUNITY.position -> tab.setIcon(R.drawable.ic_baseline_people_24)
                Tabs.PROFILE.position -> tab.setIcon(R.drawable.ic_baseline_person_pin_24)
            }
        }.attach()

        // Modify color of icon if tab is selected
        mainActivityTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val color: Int = ContextCompat.getColor(this@MainActivity, R.color.colorAccentLight)
                tab?.icon?.let { DrawableCompat.setTint(it, color) }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val color: Int =
                    ContextCompat.getColor(this@MainActivity, R.color.colorTextSecondary)
                tab?.icon?.let { DrawableCompat.setTint(it, color) }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        // Then choose the page to display
        mainActivityViewPager.currentItem = Tabs.PROFILE.position
    }

    //--------------------------------------------------------------------------------------

    // FAB disappear and appear when a page is scrolled or selected
    private fun animateFAB() {
        mainActivityViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                pageSelected = position
                if (Tabs.PROFILE.position == pageSelected) {
                    mainActivityFABEditProfile.show()
                    mainActivityFABDisconnect.show()
                } else {
                    mainActivityFABEditProfile.hide()
                    mainActivityFABDisconnect.hide()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_IDLE && Tabs.PROFILE.position == pageSelected) {
                    mainActivityFABEditProfile.show()
                    mainActivityFABDisconnect.show()
                }
                if (state == ViewPager2.SCROLL_STATE_DRAGGING && Tabs.PROFILE.position == pageSelected) {
                    mainActivityFABEditProfile.hide()
                    mainActivityFABDisconnect.hide()
                }
            }
        })
    }

    //--------------------------------------------------------------------------------------

    private fun editProfile() {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra(EDIT, ProfileFragment::class.java.name)
        startActivityForResult(intent, RC_EDIT_PROFILE)
    }

    private fun disconnectCurrentUser() {
        // Create an alert dialog to prevent the user
        AlertDialog.Builder(this, R.style.AlertDialogDisconnectTheme)
            .setMessage(R.string.disconnect)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                AuthUI.getInstance().signOut(this).addOnSuccessListener {
                    val intent = Intent(this, ConnectionActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
            }
            .show()
    }
}