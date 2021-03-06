package com.jpz.workoutnotebook.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.adapters.ViewPagerAdapter
import com.jpz.workoutnotebook.databinding.ActivityMainBinding
import com.jpz.workoutnotebook.fragments.mainactivity.CalendarFragment
import com.jpz.workoutnotebook.fragments.mainactivity.CommunityFragment
import com.jpz.workoutnotebook.fragments.mainactivity.SportsFragment
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.utils.MyUtils
import com.jpz.workoutnotebook.utils.ZoomOutPageTransformer
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity(), SportsFragment.SportsFragmentButtonListener,
    CalendarFragment.CalendarListener, CommunityFragment.CommunityListener, View.OnClickListener {

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
        const val HISTORICAL_FRAGMENT = "HISTORICAL_FRAGMENT"
        const val SEARCH_FRAGMENT = "SEARCH_FRAGMENT"
        const val TRAINING_SESSION_FRAGMENT = "TRAINING_SESSION_FRAGMENT"
        const val TRAINING_SESSION = "TRAINING_SESSION"
        const val EXERCISES = "EXERCISES"
        const val WORKOUTS = "WORKOUTS"
        const val FOLLOW = "FOLLOW"
        const val IS_FOLLOWED = "IS_FOLLOWED"
    }

    private lateinit var binding: ActivityMainBinding

    private var pageSelected = 0
    private val myUtils: MyUtils by inject()

    private var startActivityForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        configureToolbar()
        configureViewPagerAdapter()
        configureTabLayout()
        animateFAB()

        // Used to handle intent from startForResultEditActivityForProfile()
        startActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    myUtils.showSnackBar(
                        binding.mainActivityCoordinatorLayout, R.string.user_data_updated
                    )
                } else if (result.resultCode == RESULT_CANCELED) {
                    myUtils.showSnackBar(
                        binding.mainActivityCoordinatorLayout, R.string.update_data_canceled
                    )
                }
            }

        binding.mainActivityFABEditProfile.setOnClickListener(this)
        binding.mainActivityFABAddCalendar.setOnClickListener(this)
        binding.mainActivityFABSearchCommunity.setOnClickListener(this)
    }

    //--------------------------------------------------------------------------------------
    // UI

    private fun configureToolbar() {
        // Get the toolbar view inside the activity layout
        setSupportActionBar(binding.includedLayout.toolbar)
    }

    private fun configureViewPagerAdapter() {
        binding.mainActivityViewPager.adapter = ViewPagerAdapter(this)
        binding.mainActivityViewPager.offscreenPageLimit = 1
        binding.mainActivityViewPager.setPageTransformer(ZoomOutPageTransformer())
    }

    private fun configureTabLayout() {
        val colorIconWhite: Int =
            ContextCompat.getColor(this@MainActivity, R.color.colorTextSecondary)

        TabLayoutMediator(
            binding.mainActivityTabLayout,
            binding.mainActivityViewPager
        ) { tab, position ->
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
        binding.mainActivityTabLayout
            .addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
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
        binding.mainActivityTabLayout.getTabAt(Tabs.SPORTS.position)?.icon?.let {
            DrawableCompat.setTint(it, colorIconWhite)
        }
    }

    override fun onBackPressed() {
        if (binding.mainActivityViewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            binding.mainActivityViewPager.currentItem =
                binding.mainActivityViewPager.currentItem - 1
        }
    }

    // FAB disappear and appear when a page is scrolled or selected
    private fun animateFAB() {
        binding.mainActivityViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                pageSelected = position

                when (pageSelected) {
                    Tabs.PROFILE.position -> binding.mainActivityFABEditProfile.show()
                    Tabs.CALENDAR.position -> binding.mainActivityFABAddCalendar.show()
                    Tabs.COMMUNITY.position -> binding.mainActivityFABSearchCommunity.show()
                    else -> {
                        binding.mainActivityFABEditProfile.hide()
                        binding.mainActivityFABAddCalendar.hide()
                        binding.mainActivityFABSearchCommunity.hide()
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                when (state) {
                    ViewPager2.SCROLL_STATE_IDLE ->
                        when (pageSelected) {
                            Tabs.PROFILE.position -> binding.mainActivityFABEditProfile.show()
                            Tabs.CALENDAR.position -> binding.mainActivityFABAddCalendar.show()
                            Tabs.COMMUNITY.position -> binding.mainActivityFABSearchCommunity.show()
                        }

                    ViewPager2.SCROLL_STATE_DRAGGING ->
                        when (pageSelected) {
                            Tabs.PROFILE.position -> binding.mainActivityFABEditProfile.hide()
                            Tabs.CALENDAR.position -> binding.mainActivityFABAddCalendar.hide()
                            Tabs.COMMUNITY.position -> binding.mainActivityFABSearchCommunity.hide()
                        }

                    ViewPager2.SCROLL_STATE_SETTLING -> {
                        when (pageSelected) {
                            Tabs.PROFILE.position -> binding.mainActivityFABEditProfile.hide()
                            Tabs.CALENDAR.position -> binding.mainActivityFABAddCalendar.hide()
                            Tabs.COMMUNITY.position -> binding.mainActivityFABSearchCommunity.hide()
                        }
                    }
                }
            }
        })
    }

    //--------------------------------------------------------------------------------------
    // Start EditActivity

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

    private fun startEditActivityForHistorical(trainingSession: TrainingSession?) {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra(EDIT, HISTORICAL_FRAGMENT)
        intent.putExtra(TRAINING_SESSION, trainingSession)
        startActivity(intent)
    }

    private fun startEditActivityForTrainingSession(trainingSession: TrainingSession?) {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra(EDIT, TRAINING_SESSION_FRAGMENT)
        intent.putExtra(TRAINING_SESSION, trainingSession)
        startActivity(intent)
    }

    private fun startForResultEditActivityForProfile() {
        val intent = Intent(this, EditActivity::class.java)
        intent.putExtra(EDIT, EDIT_PROFILE_FRAGMENT)
        startActivityForResult.launch(intent)
    }

    //--------------------------------------------------------------------------------------
    // Start FollowingActivity

    private fun startFollowingActivityForSearch() {
        val intent = Intent(this, FollowingActivity::class.java)
        intent.putExtra(EDIT, SEARCH_FRAGMENT)
        startActivity(intent)
    }

    private fun startFollowingActivityToDisplayFollow(
        user: User?, viewClicked: View?, isFollowed: Boolean
    ) {
        val intent = Intent(this, FollowingActivity::class.java)
        intent.putExtra(FOLLOW, user)
        intent.putExtra(IS_FOLLOWED, isFollowed)

        // Start Animation if the version > Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Animation options
            val options: ActivityOptions = ActivityOptions.makeSceneTransitionAnimation(
                this, viewClicked, getString(R.string.animation_profile_photo_list_to_detail)
            )
            startActivity(intent, options.toBundle())
        } else {
            // No animation if the version < Lollipop
            startActivity(intent)
        }
    }

    //--------------------------------------------------------------------------------------

    // Implement listener from SportsFragment to consult the list of exercises or workouts
    override fun onClickedExerciseOrWorkoutButton(button: String?) {
        if (button == getString(R.string.exercises)) {
            startEditActivityForExerciseOrWorkout(EXERCISES)
        } else {
            startEditActivityForExerciseOrWorkout(WORKOUTS)
        }
    }

    // Implement listener from SportsFragment to start the training session
    override fun onClickedTrainingSessionButton(trainingSession: TrainingSession) {
        startEditActivityForTrainingSession(trainingSession)
    }

    // Implement listener from CalendarFragment to update a training session
    override fun updateATrainingSession(trainingSession: TrainingSession) {
        startEditActivityForCalendar(trainingSession)
    }

    // Implement listener from CalendarFragment to show a snackBar below the FAB
    override fun cannotUpdatePreviousTrainingSession() {
        myUtils.showSnackBar(
            binding.mainActivityCoordinatorLayout,
            R.string.cannot_create_update_training_session_with_past_date
        )
    }

    // Implement listener from CalendarFragment to show a snackBar below the FAB
    override fun cannotUpdateCompletedTrainingSession() {
        myUtils.showSnackBar(
            binding.mainActivityCoordinatorLayout, R.string.cannot_update_completed_training_session
        )
    }

    // Implement listener from CalendarFragment to consult a training session
    override fun consultATrainingSession(trainingSession: TrainingSession) {
        startEditActivityForHistorical(trainingSession)
    }

    // Implement listener from CommunityFragment to display the profile of a followed people or a follower
    override fun displayFollow(user: User?, viewClicked: View?, isFollowed: Boolean) {
        startFollowingActivityToDisplayFollow(user, viewClicked, isFollowed)
    }

    //--------------------------------------------------------------------------------------

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mainActivityFABEditProfile -> startForResultEditActivityForProfile()
            R.id.mainActivityFABAddCalendar -> startEditActivityForCalendar(null)
            R.id.mainActivityFABSearchCommunity -> startFollowingActivityForSearch()
        }
    }
}