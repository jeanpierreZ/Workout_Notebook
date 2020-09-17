package com.jpz.workoutnotebook.api

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class UserStoragePhoto {

    companion object {
        private const val REPOSITORY_STORAGE_REF = "photos"
    }

    fun storageRef(userId: String): StorageReference {
        // Instance of FirebaseStorage with point to the root reference
        val storageRef = FirebaseStorage.getInstance().reference
        // Use variables to create child values
        // Points to "photos/userID"
        val photosRef = storageRef.child(REPOSITORY_STORAGE_REF)
        // The filename of the user's photo is the user's id
        return photosRef.child(userId)
    }
}