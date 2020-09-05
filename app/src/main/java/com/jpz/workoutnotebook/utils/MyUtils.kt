package com.jpz.workoutnotebook.utils

import android.content.Context
import android.content.Intent
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import com.jpz.workoutnotebook.activities.MainActivity


class MyUtils {

    fun showSnackBar(coordinatorLayout: CoordinatorLayout, text: Int) {
        Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_SHORT).show()
    }

    //--------------------------------------------------------------------------------------
    // Navigation

    fun startMainActivity(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }

}