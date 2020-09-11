package com.jpz.workoutnotebook.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.jpz.workoutnotebook.api.UserHelper
import com.jpz.workoutnotebook.models.User

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userHelper = UserHelper()

    // --- CREATE ---

    fun createUser(user: User) = userHelper.createUser(user)

    // --- READ ---

    fun getUser(userId: String) = userHelper.getUser(userId)

    // --- QUERY ---

    // Recover data from user in real-time
    fun getCurrentUser(userId: String) = userHelper.getCurrentUser(userId)

    // --- UPDATE ---

    fun updateUser(user: User) = userHelper.updateUser(user)

}