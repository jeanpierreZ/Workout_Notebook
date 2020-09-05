package com.jpz.workoutnotebook.utils

import androidx.annotation.Nullable
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.FirebaseUser


class FirebaseUtils {

    // Method to recover the user currently connected
    @Nullable
    fun getCurrentUser(): FirebaseUser? = FirebaseAuth.getInstance().currentUser

    // Method to find out if the user is connected
    fun isCurrentUserLogged(): Boolean = getCurrentUser() != null

}