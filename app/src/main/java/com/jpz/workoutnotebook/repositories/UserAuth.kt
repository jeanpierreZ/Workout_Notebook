package com.jpz.workoutnotebook.repositories

import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class UserAuth {

    fun getInstanceOfAuthUI() = AuthUI.getInstance()

    // Method to recover the user currently connected
    fun getCurrentUser(): FirebaseUser? = FirebaseAuth.getInstance().currentUser

    // Method to find out if the user is connected
    fun isCurrentUserLogged(): Boolean = getCurrentUser() != null

    // Method to get the user uid
    fun getUserUid(): String? = getCurrentUser()?.uid
}