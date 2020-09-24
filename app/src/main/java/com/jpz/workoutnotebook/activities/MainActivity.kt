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
import com.jpz.workoutnotebook.fragments.SportsFragment
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.utils.RequestCodes.Companion.RC_EDIT_EXERCISE
import com.jpz.workoutnotebook.utils.RequestCodes.Companion.RC_EDIT_PROFILE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity(), SportsFragment.SportsFragmentButtonListener {

    enum class Tabs(val position: Int) {
        SPORTS(0),
        CALENDAR(1),
        STATISTICS(2),
        COMMUNITY(3),
        PROFILE(4)
    }

    companion object {
        const val EDIT = "EDIT"
        const val PROFILE_FRAGMENT = "PROFILE_FRAGMENT"
        const val EXERCISE_FRAGMENT = "EXERCISE_FRAGMENT"
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
            startEditActivity(PROFILE_FRAGMENT, RC_EDIT_PROFILE)
        }
        mainActivityFABDisconnect.setOnClickListener {
            disconnectCurrentUser()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            RC_EDIT_PROFILE -> {
                if (resultCode == RESULT_OK) {
                    myUtils.showSnackBar(
                        mainActivityCoordinatorLayout, R.string.user_data_updated
                    )
                } else if (resultCode == RESULT_CANCELED) {
                    myUtils.showSnackBar(
                        mainActivityCoordinatorLayout, R.string.update_data_canceled
                    )
                }
            }

            RC_EDIT_EXERCISE ->
                if (resultCode == RESULT_OK) {
                    myUtils.showSnackBar(
                        mainActivityCoordinatorLayout, R.string.exercise_added
                    )
                } else if (resultCode == RESULT_CANCELED) {
                    myUtils.showSnackBar(
                        mainActivityCoordinatorLayout, R.string.add_exercise_canceled
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
        val colorIconWhite: Int =
            ContextCompat.getColor(this@MainActivity, R.color.colorTextSecondary)

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
                tab?.icon?.let { DrawableCompat.setTint(it, colorIconWhite) }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val colorIconBlack: Int =
                    ContextCompat.getColor(this@MainActivity, R.color.colorTextPrimary)
                tab?.icon?.let { DrawableCompat.setTint(it, colorIconBlack) }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

        // Then display the tab icon in white for the first page
        mainActivityTabLayout.getTabAt(Tabs.SPORTS.position)?.icon?.let {
            DrawableCompat.setTint(
                it,
                colorIconWhite
            )
        }
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

    private fun startEditActivity(edit: String, requestCode: Int) {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra(EDIT, edit)
        startActivityForResult(intent, requestCode)
    }

    //--------------------------------------------------------------------------------------

    private fun disconnectCurrentUser() {
        // Create an alert dialog to prevent the user
        AlertDialog.Builder(this)
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

    //--------------------------------------------------------------------------------------

    // Implement listener from SportsFragment to consult the list of exercises
    override fun onClickedSportsFragmentButton(button: String?) {
        startEditActivity(EXERCISE_FRAGMENT, RC_EDIT_EXERCISE)
    }
}