package com.jpz.workoutnotebook.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jpz.workoutnotebook.models.User


class UserHelper {

    companion object {
        private const val COLLECTION_NAME = "users"
        private const val FIELD_USER_ID = "userId"
        fun getUsersCollection(): CollectionReference? =
            Firebase.firestore.collection(COLLECTION_NAME)
    }

    // --- CREATE ---

    fun createUser(user: User): Task<Void>? {
        return getUsersCollection()?.document(user.userId)?.set(user)
    }

    // --- READ ---

    fun getUser(userId: String): Task<DocumentSnapshot>? =
        getUsersCollection()?.document(userId)?.get()

    // --- QUERY ---

    // Recover data from user in real-time
    fun getCurrentUser(userId: String): Query? =
        getUsersCollection()?.whereEqualTo(FIELD_USER_ID, userId)

    // --- UPDATE ---

    fun updateUser(user: User): Task<Void>? {
        return getUsersCollection()?.document(user.userId)?.set(user)
    }

}