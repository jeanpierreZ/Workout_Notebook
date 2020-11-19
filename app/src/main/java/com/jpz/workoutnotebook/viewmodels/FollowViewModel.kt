package com.jpz.workoutnotebook.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.Query
import com.jpz.workoutnotebook.repositories.FollowRepository

class FollowViewModel(private val followRepository: FollowRepository) : ViewModel() {

    // --- QUERY ---

    // Recover the list of follow of the user in real-time
    fun getListOfFollow(userId: String): Query? = followRepository.getListOfFollow(userId)
}