package com.jpz.workoutnotebook.utils

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar


class MyUtils {

    fun showSnackBar(coordinatorLayout: CoordinatorLayout, text: Int) {
        Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_SHORT).show()
    }

    fun showSnackBar(coordinatorLayout: CoordinatorLayout, text: String) {
        Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_SHORT).show()
    }

    fun closeFragment(progressBar: ProgressBar, activity: Activity) {
        progressBar.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({ activity.onBackPressed() }, 2000)
    }
}