package com.jpz.workoutnotebook.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.jpz.workoutnotebook.models.User


class UserRepository {

    companion object {
        private const val COLLECTION_NAME = "users"
        private const val FIELD_USER_ID = "userId"
        fun getUsersCollection(): CollectionReference? =
            FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
    }

    // --- CREATE ---

    // Creating the document if it does not already exist or update the userId (which is the same that auth.uid).
    fun createUser(userId: String, data: HashMap<String, String>) =
        getUsersCollection()?.document(userId)?.set(data, SetOptions.merge())

    // --- READ ---

    fun getUser(userId: String): Task<DocumentSnapshot>? =
        getUsersCollection()?.document(userId)?.get()

    // --- QUERY ---

    // Recover data from user in real-time
    fun getCurrentUser(userId: String): Query? =
        getUsersCollection()?.whereEqualTo(FIELD_USER_ID, userId)

    // --- UPDATE ---

    fun updateUser(user: User) = getUsersCollection()?.document(user.userId)?.set(user)
}