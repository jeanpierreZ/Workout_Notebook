package com.jpz.workoutnotebook.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.MainActivity.Companion.EDIT
import com.jpz.workoutnotebook.activities.MainActivity.Companion.EXERCISE_FRAGMENT
import com.jpz.workoutnotebook.activities.MainActivity.Companion.PROFILE_FRAGMENT
import com.jpz.workoutnotebook.fragments.EditExerciseFragment
import com.jpz.workoutnotebook.fragments.EditProfileFragment
import com.jpz.workoutnotebook.fragments.ExerciseFragment
import com.jpz.workoutnotebook.utils.MyUtils
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.toolbar.*
import org.koin.android.ext.android.inject

class EditActivity : AppCompatActivity(), ExerciseFragment.ExerciseListener {

    private var edit: String? = null

    private val myUtils: MyUtils by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        configureToolbar()

        edit = intent.getStringExtra(EDIT)

        displayProfileOrExerciseFragment()
    }

    //--------------------------------------------------------------------------------------
    // UI

    private fun configureToolbar() {
        // Get the toolbar view inside the activity layout
        setSupportActionBar(toolbar)
    }

    //--------------------------------------------------------------------------------------

    private fun displayProfileOrExerciseFragment() {
        var fragment = Fragment()
        val editProfileFragment = EditProfileFragment()
        val exerciseFragment = ExerciseFragment()

        when (edit) {
            PROFILE_FRAGMENT -> fragment = editProfileFragment
            EXERCISE_FRAGMENT -> fragment = exerciseFragment
            else -> myUtils.showSnackBar(
                editActivityCoordinatorLayout, R.string.user_data_recovery_error
            )
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.editActivityContainer, fragment)
            .commit()
    }

    private fun displayEditExerciseFragment() {
        val editExerciseFragment = EditExerciseFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.editActivityContainer, editExerciseFragment)
            .commit()
    }

    //--------------------------------------------------------------------------------------

    override fun onClickedExercise(edit: String, id: String?) {
        displayEditExerciseFragment()
    }
}