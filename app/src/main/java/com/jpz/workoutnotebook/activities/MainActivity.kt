package com.jpz.workoutnotebook.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ViewPagerAdapter
import com.jpz.workoutnotebook.fragments.CalendarFragment
import com.jpz.workoutnotebook.fragments.SportsFragment
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.utils.RequestCodes.Companion.RC_EDIT_PROFILE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity(), SportsFragment.SportsFragmentButtonListener,
    CalendarFragment.TrainingSessionListener,
    View.OnClickListener {

    enum class Tabs(val position: Int) {
        SPORTS(0),
        CALENDAR(1),
        STATISTICS(2),
        COMMUNITY(3),
        PROFILE(4)
    }

    companion object {
        const val EDIT = "EDIT"
        const val EDIT_PROFILE_FRAGMENT = "EDIT_PROFILE_FRAGMENT"
        const val EDIT_CALENDAR_FRAGMENT = "EDIT_CALENDAR_FRAGMENT"
        const val TRAINING_SESSION = "TRAINING_SESSION"
        const val EXERCISES = "EXERCISES"
        const val WORKOUTS = "WORKOUTS"
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

        mainActivityFABEditProfile.setOnClickListener(this)
        mainActivityFABAddCalendar.setOnClickListener(this)
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
                it, colorIconWhite
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

                when (pageSelected) {
                    Tabs.PROFILE.position -> mainActivityFABEditProfile.show()
                    Tabs.CALENDAR.position -> mainActivityFABAddCalendar.show()
                    else -> {
                        mainActivityFABEditProfile.hide(); mainActivityFABAddCalendar.hide()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager2.SCROLL_STATE_IDLE ->
                        when (pageSelected) {
                            Tabs.PROFILE.position -> mainActivityFABEditProfile.show()
                            Tabs.CALENDAR.position -> mainActivityFABAddCalendar.show()
                        }

                    ViewPager2.SCROLL_STATE_DRAGGING ->
                        when (pageSelected) {
                            Tabs.PROFILE.position -> mainActivityFABEditProfile.hide()
                            Tabs.CALENDAR.position -> mainActivityFABAddCalendar.hide()
                        }

                    ViewPager2.SCROLL_STATE_SETTLING -> {
                        when (pageSelected) {
                            Tabs.PROFILE.position -> mainActivityFABEditProfile.hide()
                            Tabs.CALENDAR.position -> mainActivityFABAddCalendar.hide()
                        }
                    }
                }
            }
        })
    }

    //--------------------------------------------------------------------------------------

    private fun startEditActivityForExerciseOrWorkout(fragmentName: String) {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra(EDIT, fragmentName)
        startActivity(intent)
    }

    private fun startEditActivityForCalendar(trainingSession: TrainingSession?) {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra(EDIT, EDIT_CALENDAR_FRAGMENT)
        intent.putExtra(TRAINING_SESSION, trainingSession)
        startActivity(intent)
    }

    private fun startEditActivityForProfile() {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra(EDIT, EDIT_PROFILE_FRAGMENT)
        startActivityForResult(intent, RC_EDIT_PROFILE)
    }

    //--------------------------------------------------------------------------------------

    // Implement listener from SportsFragment to consult the list of exercises or workouts
    override fun onClickedSportsFragmentButton(button: String?) {
        if (button == getString(R.string.exercises)) {
            startEditActivityForExerciseOrWorkout(EXERCISES)
        } else {
            startEditActivityForExerciseOrWorkout(WORKOUTS)
        }
    }

    // Implement listener from CalendarFragment to update a training session
    override fun updateATrainingSession(trainingSession: TrainingSession) {
        startEditActivityForCalendar(trainingSession)
    }

    // Implement listener from CalendarFragment to show a snackBar below the FAB
    override fun cannotUpdatePreviousTrainingSession() {
        myUtils.showSnackBar(
            mainActivityCoordinatorLayout, R.string.cannot_create_update_training_session_with_past_date
        )
    }

    //--------------------------------------------------------------------------------------

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mainActivityFABEditProfile -> startEditActivityForProfile()
            R.id.mainActivityFABAddCalendar -> startEditActivityForCalendar(null)
        }
    }
}