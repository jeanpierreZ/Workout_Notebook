package com.jpz.workoutnotebook.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.jpz.workoutnotebook.models.Exercise

class ExerciseRepository {

    companion object {
        private const val COLLECTION_NAME = "exercises"
        private const val EXERCISE_ID_FIELD = "exerciseId"
        private const val EXERCISE_NAME_FIELD = "exerciseName"
    }

    // --- CREATE ---

    fun createExercise(userId: String, exercise: Exercise): Task<DocumentReference> =
        UserRepository.getUsersCollection()
            .document(userId)
            .collection(COLLECTION_NAME)
            .add(exercise)

    // --- READ ---

    fun getExercise(userId: String, exerciseId: String): Task<DocumentSnapshot> =
        UserRepository.getUsersCollection()
            .document(userId)
            .collection(COLLECTION_NAME)
            .document(exerciseId)
            .get()

    // --- QUERY ---

    fun getListOfExercises(userId: String): Query =
        UserRepository.getUsersCollection().document(userId).collection(COLLECTION_NAME)

    fun getOrderedListOfExercises(userId: String): Query =
        UserRepository.getUsersCollection().document(userId).collection(COLLECTION_NAME)
            .orderBy(EXERCISE_NAME_FIELD, Query.Direction.ASCENDING)

    // --- UPDATE ---

    fun updateExerciseIdAfterCreate(userId: String, documentReference: DocumentReference) =
        UserRepository.getUsersCollection()
            .document(userId)
            .collection(COLLECTION_NAME)
            .document(documentReference.id)
            // Use SetOptions.merge() to only update the exerciseId
            .set(hashMapOf(EXERCISE_ID_FIELD to documentReference.id), SetOptions.merge())

    fun updateExercise(userId: String, exercise: Exercise) =
        exercise.exerciseId?.let {
            UserRepository.getUsersCollection()
                .document(userId)
                .collection(COLLECTION_NAME)
                .document(it)
                .set(exercise)
        }
}