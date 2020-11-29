package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jpz.workoutnotebook.repositories.FollowingRepository

class FollowingViewModel(private val followingRepository: FollowingRepository) : ViewModel() {

    companion object {
        private val TAG = FollowViewModel::class.java.simpleName
    }

    // --- CREATE ---

    fun addFollower(userId: String, followedId: String) =
        followingRepository.addFollower(userId, followedId)
            .addOnFailureListener { e -> Log.e(TAG, "Error writing document", e) }

    // --- QUERY ---

    // Recover the list of followers
    fun getListOfFollowers(userId: String) = followingRepository.getListOfFollowers(userId)
}