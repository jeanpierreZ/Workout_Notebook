package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.jpz.workoutnotebook.repositories.FollowRepository

class FollowViewModel(
    private val followRepository: FollowRepository, userViewModel: UserViewModel
) : ViewModel() {

    companion object {
        private val TAG = FollowViewModel::class.java.simpleName
    }

    val userId: String? = userViewModel.getUserUid()

    // --- CREATE ---

    fun follow(followedId: String) = userId?.let {
        followRepository.follow(it, followedId)
            .addOnFailureListener { e -> Log.e(TAG, "Error writing document", e) }
    }

    // --- QUERY ---

    // Recover the list of people followed by the user
    fun getListOfPeopleFollowed(): Query? =
        userId?.let { followRepository.getListOfPeopleFollowed(it) }

    // --- DELETE ---

    fun noLongerFollow(followedId: String) = userId?.let {
        followRepository.noLongerFollow(it, followedId)
            .addOnFailureListener { e -> Log.e(TAG, "Error deleted document", e) }
    }
}