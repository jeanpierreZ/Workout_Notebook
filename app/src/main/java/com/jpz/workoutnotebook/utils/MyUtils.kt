package com.jpz.workoutnotebook.utils

import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar


class MyUtils {

    fun showSnackBar(coordinatorLayout: CoordinatorLayout, text: Int) {
        Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_SHORT).show()
    }
}