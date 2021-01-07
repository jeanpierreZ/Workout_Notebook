package com.jpz.workoutnotebook.repositories

import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions

class FollowRepository {
    // Class for people the current user follows

    companion object {
        private const val COLLECTION_NAME = "follows"
        private const val FOLLOWED_ID_FIELD = "followedId"
    }

    // --- CREATE ---

    fun follow(userId: String, followedId: String) =
        UserRepository.getUsersCollection()
            .document(userId)
            .collection(COLLECTION_NAME)
            .document(followedId)
            .set(hashMapOf(FOLLOWED_ID_FIELD to followedId), SetOptions.merge())

    // --- QUERY ---

    // Recover the list of people followed by the user
    fun getListOfPeopleFollowed(userId: String): Query =
        UserRepository.getUsersCollection().document(userId).collection(COLLECTION_NAME)

    // --- DELETE ---

    fun noLongerFollow(userId: String, followedId: String) =
        UserRepository.getUsersCollection()
            .document(userId)
            .collection(COLLECTION_NAME)
            .document(followedId)
            .delete()
}