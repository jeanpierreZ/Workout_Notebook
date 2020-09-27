package com.jpz.workoutnotebook.api

import android.app.Activity
import android.content.Intent
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat.startActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jpz.workoutnotebook.activities.ConnectionActivity


class UserAuth {
    // Method to recover the user currently connected
    @Nullable
    fun getCurrentUser(): FirebaseUser? = FirebaseAuth.getInstance().currentUser

    // Method to find out if the user is connected
    fun isCurrentUserLogged(): Boolean = getCurrentUser() != null

    // Method to disconnect the user
    fun signOut(activity: Activity) {
        AuthUI.getInstance().signOut(activity).addOnSuccessListener {
            val intent = Intent(activity, ConnectionActivity::class.java)
            startActivity(activity, intent, null)
            activity.finish()
        }
    }
}