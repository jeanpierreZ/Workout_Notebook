package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jpz.workoutnotebook.models.User
import com.jpz.workoutnotebook.repositories.UserAuth
import com.jpz.workoutnotebook.repositories.UserRepository
import com.jpz.workoutnotebook.repositories.UserStoragePhoto

class UserViewModel(
    private val userRepository: UserRepository,
    private val userStoragePhoto: UserStoragePhoto,
    private val userAuth: UserAuth
) : ViewModel() {
    // Class of ViewModel used to access Firebase Auth, Firebase Storage and Firestore from the repositories

    val userId: String? = getUserUid()

    // --- --- FIREBASE AUTH --- ---

    fun getInstanceOfAuthUI() = userAuth.getInstanceOfAuthUI()

    fun getCurrentUser() = userAuth.getCurrentUser()

    fun isCurrentUserLogged() = userAuth.isCurrentUserLogged()

    fun getUserUid() = userAuth.getUserUid()

    // --- --- FIREBASE STORAGE --- ---

    // Get storageRef from UserStoragePhoto
    fun storageRef() = userId?.let { userStoragePhoto.storageRef(it) }

    // --- --- FIREBASE FIRESTORE --- ---

    // --- CREATE ---

    fun createUser(userId: String, data: HashMap<String, String>) =
        userRepository.createUser(userId, data)
            .addOnFailureListener { e -> Log.e("createUser", "Error writing document", e) }

    // --- READ ---

    fun getUser() = userId?.let {
        userRepository.getUser(it)
            .addOnFailureListener { e -> Log.d("getUser", "get failed with ", e) }
    }

    // Get the followed and follower user
    fun getFollow(followId: String) = userRepository.getUser(followId)
        .addOnFailureListener { e -> Log.d("getUser", "get failed with ", e) }

    // --- QUERY ---

    // Recover data from user in real-time
    fun getCurrentUserData() = userId?.let { userRepository.getCurrentUserData(it) }

    // --- UPDATE ---

    fun updateUser(user: User) = userRepository.updateUser(user)
}