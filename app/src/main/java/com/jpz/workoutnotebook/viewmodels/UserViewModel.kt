package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jpz.workoutnotebook.api.UserHelper
import com.jpz.workoutnotebook.models.User

class UserViewModel(private val userHelper: UserHelper) : ViewModel() {

    // --- CREATE ---

    fun createUser(userId: String, data: HashMap<String, out Any>) =
        userHelper.createUser(userId, data)?.addOnFailureListener { e ->
            Log.e("createUser", "Error writing document", e)
        }

    // --- READ ---

    fun getUser(userId: String) = userHelper.getUser(userId)?.addOnFailureListener { e ->
        Log.d("getUser", "get failed with ", e)
    }

    // --- QUERY ---

    // Recover data from user in real-time
    fun getCurrentUser(userId: String) = userHelper.getCurrentUser(userId)

    // --- UPDATE ---

    fun updateUser(user: User) = userHelper.updateUser(user)?.addOnFailureListener { e ->
        Log.e("updateUser", "Error updating document", e)
    }
}