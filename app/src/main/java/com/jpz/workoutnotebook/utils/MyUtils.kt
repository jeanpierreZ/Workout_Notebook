package com.jpz.workoutnotebook.utils

import android.app.Activity
import android.view.View
import android.widget.ProgressBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MyUtils {

    fun showSnackBar(coordinatorLayout: CoordinatorLayout?, text: Int) {
        coordinatorLayout?.let { Snackbar.make(it, text, Snackbar.LENGTH_SHORT).show() }
    }

    fun showSnackBar(coordinatorLayout: CoordinatorLayout?, text: String) {
        coordinatorLayout?.let { Snackbar.make(it, text, Snackbar.LENGTH_SHORT).show() }
    }

    fun closeFragment(progressBar: ProgressBar, activity: Activity) {
        progressBar.visibility = View.VISIBLE
        GlobalScope.launch {
            delay(2000L)
            if (!activity.isFinishing) {
                activity.runOnUiThread { activity.onBackPressed() }
            }
        }
    }
}