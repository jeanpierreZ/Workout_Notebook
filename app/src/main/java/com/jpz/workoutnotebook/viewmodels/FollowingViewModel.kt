package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jpz.workoutnotebook.repositories.FollowingRepository

class FollowingViewModel(
    private val followingRepository: FollowingRepository, userViewModel: UserViewModel
) : ViewModel() {

    companion object {
        private val TAG = FollowViewModel::class.java.simpleName
    }

    val userId: String = userViewModel.getUserUid()

    // --- CREATE ---

    fun addFollower(followedId: String) = followingRepository.addFollower(userId, followedId)
        .addOnFailureListener { e -> Log.e(TAG, "Error writing document", e) }

    // --- QUERY ---

    // Recover the list of followers
    fun getListOfFollowers() = followingRepository.getListOfFollowers(userId)
}