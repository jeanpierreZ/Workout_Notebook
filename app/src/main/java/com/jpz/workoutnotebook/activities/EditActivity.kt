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
import com.jpz.workoutnotebook.activities.MainActivity.Companion.TRAINING_SESSION
import com.jpz.workoutnotebook.activities.MainActivity.Companion.WORKOUTS
import com.jpz.workoutnotebook.fragments.*
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.utils.MyUtils
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.ext.android.inject

class EditActivity : AppCompatActivity(), ListSportsFragment.ItemListener {

    companion object {
        private val TAG = EditActivity::class.java.simpleName
        const val IS_AN_EXERCISE = "IS_AN_EXERCISE"
        const val EXERCISE_ID = "EXERCISE_ID"
        const val WORKOUT_ID = "WORKOUT_ID"
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

        edit?.let { displayFragment(it, trainingSession) }
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

    private fun displayFragment(edit: String, trainingSession: TrainingSession?) {
        var fragment = Fragment()
        val editProfileFragment = EditProfileFragment()
        val editCalendarFragment = EditCalendarFragment()
        val listSportsFragment = ListSportsFragment()
        val bundle = Bundle()

        when (edit) {
            EDIT_PROFILE_FRAGMENT -> fragment = editProfileFragment

            EDIT_CALENDAR_FRAGMENT -> {
                fragment = editCalendarFragment
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

    private fun displayEditExerciseFragment(exerciseId: String?) {
        val editExerciseFragment = EditExerciseFragment()
        val bundle = Bundle()
        bundle.putString(EXERCISE_ID, exerciseId)
        editExerciseFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.editActivityContainer, editExerciseFragment)
            .addToBackStack(editExerciseFragment::class.java.name)
            .commit()
    }

    private fun displayEditWorkoutFragment(workoutId: String?) {
        val editWorkoutFragment = EditWorkoutFragment()
        val bundle = Bundle()
        bundle.putString(WORKOUT_ID, workoutId)
        editWorkoutFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.editActivityContainer, editWorkoutFragment)
            .addToBackStack(editWorkoutFragment::class.java.name)
            .commit()
    }

    //--------------------------------------------------------------------------------------
    // Callback from ListSportsFragment

    override fun addOrUpdateItem(isAnExercise: Boolean, item: String?) {
        if (isAnExercise) {
            displayEditExerciseFragment(item)
        } else {
            displayEditWorkoutFragment(item)
        }
    }
}