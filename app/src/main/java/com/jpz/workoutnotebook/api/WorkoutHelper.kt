package com.jpz.workoutnotebook.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.jpz.workoutnotebook.models.Workout
import java.util.*

class WorkoutHelper {

    companion object {
        private const val COLLECTION_NAME = "workouts"
        private const val WORKOUT_NAME_FIELD = "workoutName"
    }
/*
    // --- CREATE ---

    fun createWorkout(
        workoutId: String, workoutName: String, workoutDate: Date, exercisesList: ArrayList<String>?
    ): Task<Void>? {
        val workoutToCreate = Workout(workoutId, workoutName, workoutDate, exercisesList)
        return getWorkoutsCollection()?.document(workoutId)?.set(workoutToCreate)
    }
*/

    // --- READ ---

    fun getWorkout(userId: String, workoutName: String): Task<DocumentSnapshot>? =
        UserHelper.getUsersCollection()
            ?.document(userId)
            ?.collection(COLLECTION_NAME)
            ?.document(workoutName)
            ?.get()

    // --- QUERY ---

    fun getListOfExercises(userId: String): Query? =
        UserHelper.getUsersCollection()?.document(userId)?.collection(COLLECTION_NAME)

    fun getOrderedListOfExercises(userId: String): Query? =
        UserHelper.getUsersCollection()?.document(userId)?.collection(COLLECTION_NAME)
            ?.orderBy(WORKOUT_NAME_FIELD, Query.Direction.ASCENDING)

/*
    // --- UPDATE ---

    fun updateWorkout(workout: Workout): Task<Void>? {
        return getWorkoutsCollection()?.document(workout.workoutId)?.set(workout)
    }

    // --- DELETE ---

    fun deleteWorkout(workoutId: String): Task<Void?>? {
        return getWorkoutsCollection()?.document(workoutId)?.delete()
    }*/
}