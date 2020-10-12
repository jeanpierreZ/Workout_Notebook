package com.jpz.workoutnotebook.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.MainActivity.Companion.EDIT
import com.jpz.workoutnotebook.activities.MainActivity.Companion.EXERCISES
import com.jpz.workoutnotebook.activities.MainActivity.Companion.PROFILE_FRAGMENT
import com.jpz.workoutnotebook.activities.MainActivity.Companion.WORKOUTS
import com.jpz.workoutnotebook.fragments.EditExerciseFragment
import com.jpz.workoutnotebook.fragments.EditProfileFragment
import com.jpz.workoutnotebook.fragments.EditWorkoutFragment
import com.jpz.workoutnotebook.fragments.ListSportsFragment
import com.jpz.workoutnotebook.utils.MyUtils
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.ext.android.inject

class EditActivity : AppCompatActivity(), ListSportsFragment.ItemListener {

    companion object {
        const val IS_AN_EXERCISE = "IS_AN_EXERCISE"
        const val EXERCISE_NAME = "EXERCISE_NAME"
        const val WORKOUT_NAME = "WORKOUT_NAME"
    }

    private val myUtils: MyUtils by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        configureToolbar()

        val edit = intent.getStringExtra(EDIT)

        edit?.let { displayListSportsOrEditProfileFragment(it) }
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

    private fun displayListSportsOrEditProfileFragment(edit: String) {
        var fragment = Fragment()
        val editProfileFragment = EditProfileFragment()
        val listSportsFragment = ListSportsFragment()
        val bundle = Bundle()

        when (edit) {
            PROFILE_FRAGMENT -> fragment = editProfileFragment

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

    private fun displayEditExerciseFragment(exerciseName: String?) {
        val editExerciseFragment = EditExerciseFragment()
        val bundle = Bundle()
        bundle.putString(EXERCISE_NAME, exerciseName)
        editExerciseFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.editActivityContainer, editExerciseFragment)
            .addToBackStack(editExerciseFragment::class.java.name)
            .commit()
    }

    private fun displayEditWorkoutFragment(workoutName: String?) {
        val editWorkoutFragment = EditWorkoutFragment()
        val bundle = Bundle()
        bundle.putString(WORKOUT_NAME, workoutName)
        editWorkoutFragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(R.id.editActivityContainer, editWorkoutFragment)
            .addToBackStack(editWorkoutFragment::class.java.name)
            .commit()
    }

    //--------------------------------------------------------------------------------------
    // Callback from LisSportsFragment

    override fun addOrUpdateItem(isAnExercise: Boolean, itemName: String?) {
        if (isAnExercise) {
            displayEditExerciseFragment(itemName)
        } else {
            displayEditWorkoutFragment(itemName)
        }
    }
}