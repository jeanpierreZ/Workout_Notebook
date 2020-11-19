package com.jpz.workoutnotebook.repositories

import com.google.firebase.firestore.*

class FollowRepository {

    companion object {
        private const val COLLECTION_NAME = "follow"
    }

    // --- QUERY ---

    // Recover the list of follow of the user in real-time
    fun getListOfFollow(userId: String): Query? =
        UserRepository.getUsersCollection()?.document(userId)?.collection(COLLECTION_NAME)
}