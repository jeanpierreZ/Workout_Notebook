package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.repositories.UserRepository

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    // --- CREATE ---

    fun createUser(userId: String, data: HashMap<String, String>) =
        userRepository.createUser(userId, data)
            .addOnFailureListener { e -> Log.e("createUser", "Error writing document", e) }

    // --- READ ---

    fun getUser(userId: String) = userRepository.getUser(userId)
        .addOnFailureListener { e -> Log.d("getUser", "get failed with ", e) }

    // --- QUERY ---

    // Recover data from user in real-time
    fun getCurrentUser(userId: String) = userRepository.getCurrentUser(userId)

    // --- UPDATE ---

    fun updateUser(user: User) = userRepository.updateUser(user)
        .addOnFailureListener { e -> Log.e("updateUser", "Error updating document", e) }
}