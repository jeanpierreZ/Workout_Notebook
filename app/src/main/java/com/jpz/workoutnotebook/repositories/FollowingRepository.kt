package com.jpz.workoutnotebook.repositories

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FollowingRepository {

    companion object {
        private const val COLLECTION_FOLLOWINGS_NAME = "followings"
        private const val COLLECTION_NAME = "followers"
        private const val FOLLOWER_ID_FIELD = "followerId"
        fun getFollowersCollection(): CollectionReference =
            FirebaseFirestore.getInstance().collection(COLLECTION_FOLLOWINGS_NAME)
    }

    // --- CREATE ---

    fun addFollower(userId: String, followedId: String) =
        getFollowersCollection()
            .document(followedId)
            .collection(COLLECTION_NAME)
            .document(userId)
            .set(hashMapOf(FOLLOWER_ID_FIELD to userId), SetOptions.merge())
}