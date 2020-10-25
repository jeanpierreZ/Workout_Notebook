package com.jpz.workoutnotebook.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.jpz.workoutnotebook.models.Workout

class WorkoutHelper {

    companion object {
        private const val COLLECTION_NAME = "workouts"
        private const val WORKOUT_ID_FIELD = "workoutId"
        private const val WORKOUT_NAME_FIELD = "workoutName"
    }

    // --- CREATE ---

    fun createWorkout(userId: String, workout: Workout): Task<DocumentReference>? =
        UserHelper.getUsersCollection()
            ?.document(userId)
            ?.collection(COLLECTION_NAME)
            ?.add(workout)

    // --- READ ---

    fun getWorkout(userId: String, workoutId: String): Task<DocumentSnapshot>? =
        UserHelper.getUsersCollection()
            ?.document(userId)
            ?.collection(COLLECTION_NAME)
            ?.document(workoutId)
            ?.get()

    // --- QUERY ---

    fun getListOfExercises(userId: String): Query? =
        UserHelper.getUsersCollection()?.document(userId)?.collection(COLLECTION_NAME)

    fun getOrderedListOfExercises(userId: String): Query? =
        UserHelper.getUsersCollection()?.document(userId)?.collection(COLLECTION_NAME)
            ?.orderBy(WORKOUT_NAME_FIELD, Query.Direction.ASCENDING)

    // --- UPDATE ---

    fun updateWorkoutIdAfterCreate(
        userId: String, documentReference: DocumentReference
    ): Task<Void>? =
        UserHelper.getUsersCollection()
            ?.document(userId)
            ?.collection(COLLECTION_NAME)
            ?.document(documentReference.id)
            // Use SetOptions.merge() to only update the workoutId
            ?.set(hashMapOf(WORKOUT_ID_FIELD to documentReference.id), SetOptions.merge())

    fun updateWorkout(userId: String, workout: Workout): Task<Void>? =
        workout.workoutId?.let {
            UserHelper.getUsersCollection()
                ?.document(userId)
                ?.collection(COLLECTION_NAME)
                ?.document(it)
                ?.set(workout)
        }
}