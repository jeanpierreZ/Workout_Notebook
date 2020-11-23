package com.jpz.workoutnotebook.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.MainActivity.Companion.EDIT
import com.jpz.workoutnotebook.activities.MainActivity.Companion.EDIT_CALENDAR_FRAGMENT
import com.jpz.workoutnotebook.activities.MainActivity.Companion.EDIT_PROFILE_FRAGMENT
import com.jpz.workoutnotebook.activities.MainActivity.Companion.EXERCISES
import com.jpz.workoutnotebook.activities.MainActivity.Companion.FOLLOWER
import com.jpz.workoutnotebook.activities.MainActivity.Companion.FOLLOWER_FRAGMENT
import com.jpz.workoutnotebook.activities.MainActivity.Companion.HISTORICAL_FRAGMENT
import com.jpz.workoutnotebook.activities.MainActivity.Companion.SEARCH_FRAGMENT
import com.jpz.workoutnotebook.activities.MainActivity.Companion.TRAINING_SESSION
import com.jpz.workoutnotebook.activities.MainActivity.Companion.TRAINING_SESSION_FRAGMENT
import com.jpz.workoutnotebook.activities.MainActivity.Companion.WORKOUTS
import com.jpz.workoutnotebook.fragments.*
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.models.Workout
import com.jpz.workoutnotebook.utils.MyUtils
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.ext.android.inject

class EditActivity : AppCompatActivity(), ListSportsFragment.ItemListener {

    companion object {
        private val TAG = EditActivity::class.java.simpleName
        const val IS_AN_EXERCISE = "IS_AN_EXERCISE"
        const val EXERCISE = "EXERCISE"
        const val WORKOUT = "WORKOUT"
    }

    private val myUtils: MyUtils by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        configureToolbar()

        // To know the fragment to display (called from MainActivity)
        val edit = intent.getStringExtra(EDIT)
        Log.d(TAG, "edit = $edit")

        // The training session from the calendar
        val trainingSession = intent.getParcelableExtra<TrainingSession>(TRAINING_SESSION)
        Log.d(TAG, "trainingSession = $trainingSession")

        // The follower from communityFragment
        val follower = intent.getParcelableExtra<User>(FOLLOWER)
        Log.d(TAG, "follower = $follower")

        edit?.let { displayFragment(it, trainingSession, follower) }
    }

    //--------------------------------------------------------------------------------------
    // UI

    private fun configureToolbar() {
        // Get the toolbar view inside the activity layout
        setSupportActionBar(toolbar)
        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (supportFragmentManager.popBackStackImmediate()) {
                    supportFragmentManager.popBackStackImmediate()
                } else {
                    finish()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //--------------------------------------------------------------------------------------

    private fun displayFragment(edit: String, trainingSession: TrainingSession?, follower: User?) {
        var fragment = Fragment()
        val editProfileFragment = EditProfileFragment()
        val editCalendarFragment = EditCalendarFragment()
        val historicalFragment = HistoricalFragment()
        val followerFragment = FollowerFragment()
        val searchFragment = SearchFragment()
        val trainingSessionFragment = TrainingSessionFragment()
        val listSportsFragment = ListSportsFragment()
        val bundle = Bundle()

        when (edit) {
            EDIT_PROFILE_FRAGMENT -> fragment = editProfileFragment

            EDIT_CALENDAR_FRAGMENT -> {
                fragment = editCalendarFragment
                bundle.putParcelable(TRAINING_SESSION, trainingSession)
            }

            HISTORICAL_FRAGMENT -> {
                fragment = historicalFragment
                bundle.putParcelable(TRAINING_SESSION, trainingSession)
            }

            FOLLOWER_FRAGMENT -> {
                fragment = followerFragment
                bundle.putParcelable(FOLLOWER, follower)
            }

            SEARCH_FRAGMENT -> fragment = searchFragment

            TRAINING_SESSION_FRAGMENT -> {
                fragment = trainingSessionFragment
                bundle.putParcelable(TRAINING_SESSION, trainingSession)
            }

            EXERCISES -> {
                fragment = listSportsFragment
                bundle.putBoolean(IS_AN_EXERCISE, true)
            }

            WORKOUTS -> {
                fragment = listSportsFragment
                bundle.putBoolean(IS_AN_EXERCISE, false)
            }

            else -> myUtils.showSnackBar(
                editActivityCoordinatorLayout, R.string.user_data_recovery_error
            )
        }

        if (!bundle.isEmpty) {
            fragment.arguments = bundle
        }
        supportFragmentManager.beginTransaction()
            .add(R.id.editActivityContainer, fragment)
            .commit()
    }

    private fun displayEditExerciseFragment(exercise: Exercise?) {
        val editExerciseFragment = EditExerciseFragment()
        val bundle = Bundle()
        bundle.putParcelable(EXERCISE, exercise)
        editExerciseFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.editActivityContainer, editExerciseFragment)
            .addToBackStack(editExerciseFragment::class.java.name)
            .commit()
    }

    private fun displayEditWorkoutFragment(workout: Workout?) {
        val editWorkoutFragment = EditWorkoutFragment()
        val bundle = Bundle()
        bundle.putParcelable(WORKOUT, workout)
        editWorkoutFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.editActivityContainer, editWorkoutFragment)
            .addToBackStack(editWorkoutFragment::class.java.name)
            .commit()
    }

    //--------------------------------------------------------------------------------------
    // Callbacks from ListSportsFragment

    override fun addOrUpdateExercise(exercise: Exercise?) {
        displayEditExerciseFragment(exercise)
    }

    override fun addOrUpdateWorkout(workout: Workout?) {
        displayEditWorkoutFragment(workout)
    }
}