package com.jpz.workoutnotebook.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import android.widget.ProgressBar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MyUtils {

    companion object {
        const val DELAY = 2000L
    }

    fun showSnackBar(coordinatorLayout: CoordinatorLayout?, text: Int) {
        coordinatorLayout?.let { Snackbar.make(it, text, Snackbar.LENGTH_SHORT).show() }
    }

    fun showSnackBar(coordinatorLayout: CoordinatorLayout?, text: String) {
        coordinatorLayout?.let { Snackbar.make(it, text, Snackbar.LENGTH_SHORT).show() }
    }

    fun closeFragment(progressBar: ProgressBar, activity: Activity) {
        progressBar.visibility = View.VISIBLE
        GlobalScope.launch {
            delay(DELAY)
            if (!activity.isFinishing) {
                activity.runOnUiThread { activity.onBackPressed() }
            }
        }
    }

    fun isOnline(context: Context): Boolean {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connMgr?.activeNetwork ?: return false
            val activeNetwork = connMgr.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connMgr?.activeNetworkInfo?.isConnected ?: false
        }
    }
}