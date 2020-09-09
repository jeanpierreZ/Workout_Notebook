package com.jpz.workoutnotebook.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.jpz.workoutnotebook.models.User


class UserHelper {

    companion object {
        private const val COLLECTION_NAME = "users"
        fun getUsersCollection(): CollectionReference? =
            FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
    }

    // --- CREATE ---

    fun createUser(user: User): Task<Void>? {
        return getUsersCollection()?.document(user.userId)?.set(user)
    }

    // --- READ ---

    fun getUser(userId: String): Task<DocumentSnapshot>? =
        getUsersCollection()?.document(userId)?.get()

    // --- UPDATE ---

    fun updateUser(user: User): Task<Void>? {
        return getUsersCollection()?.document(user.userId)?.set(user)
    }

}