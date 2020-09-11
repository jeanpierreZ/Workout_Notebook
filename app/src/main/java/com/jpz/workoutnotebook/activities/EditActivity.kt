package com.jpz.workoutnotebook.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.activities.MainActivity.Companion.EDIT
import com.jpz.workoutnotebook.fragments.EditProfileFragment
import com.jpz.workoutnotebook.fragments.ProfileFragment
import com.jpz.workoutnotebook.utils.MyUtils
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.toolbar.*

class EditActivity : AppCompatActivity() {

    private var edit: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        configureToolbar()

        edit = intent.getStringExtra(EDIT)

        displayFragment()
    }

    //--------------------------------------------------------------------------------------
    // UI

    private fun configureToolbar() {
        // Get the toolbar view inside the activity layout
        setSupportActionBar(toolbar)
    }

    //--------------------------------------------------------------------------------------

    private fun displayFragment() {
        val editProfileFragment = EditProfileFragment()

        if (edit.equals(ProfileFragment::class.java.name)) {
            supportFragmentManager.beginTransaction()
                .add(R.id.editActivityContainer, editProfileFragment)
                .commit()
        } else {
            val myUtils = MyUtils()
            myUtils.showSnackBar(editActivityCoordinatorLayout, R.string.user_data_recovery_error)
        }
    }
}