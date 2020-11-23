package com.jpz.workoutnotebook.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.jpz.workoutnotebook.models.User

class FollowRepository {

    companion object {
        private const val COLLECTION_NAME = "follow"
        private const val USER_ID_FIELD = "userId"
    }

    // --- CREATE ---

    fun addFollow(userId: String, follow: User): Task<DocumentReference>? =
        UserRepository.getUsersCollection()
            ?.document(userId)
            ?.collection(COLLECTION_NAME)
            ?.add(follow)

    // --- QUERY ---

    // Recover the list of follow of the user in real-time
    fun getListOfFollow(userId: String): Query? =
        UserRepository.getUsersCollection()?.document(userId)?.collection(COLLECTION_NAME)

    // Recover list of all users (without the current user) in real-time
    fun getListOfUsers(userId: String): Query? =
        UserRepository.getUsersCollection()?.whereNotEqualTo(USER_ID_FIELD, userId)
}