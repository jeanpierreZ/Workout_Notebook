package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.jpz.workoutnotebook.repositories.FollowRepository

class FollowViewModel(private val followRepository: FollowRepository) : ViewModel() {

    companion object {
        private val TAG = FollowViewModel::class.java.simpleName
    }

    // --- CREATE ---

    fun follow(userId: String, followedId: String) =
        followRepository.follow(userId, followedId)
            .addOnFailureListener { e -> Log.e(TAG, "Error writing document", e) }

    // --- QUERY ---

    // Recover the list of people followed by the user
    fun getListOfPeopleFollowed(userId: String): Query =
        followRepository.getListOfPeopleFollowed(userId)

    // Recover list of all users (without the current user)
    fun getListOfUsers(userId: String) = followRepository.getListOfUsers(userId)

    // --- DELETE ---

    fun noLongerFollow(userId: String, followedId: String) =
        followRepository.noLongerFollow(userId, followedId)
            .addOnFailureListener { e -> Log.e(TAG, "Error deleted document", e) }
}