package com.jpz.workoutnotebook.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.Workout
import java.util.*

class WorkoutHelper {

    companion object {
        private const val COLLECTION_NAME = "workouts"
        private const val WORKOUT_NAME_FIELD = "workoutName"
    }

    // --- CREATE ---

    fun createWorkout(
        userId: String,
        workoutName: String?,
        workoutDate: Date?,
        exercisesList: ArrayList<Exercise>?
    ): Task<Void>? {
        val workoutToCreate = Workout(workoutName, workoutDate, exercisesList)
        return workoutName?.let {
            UserHelper.getUsersCollection()
                ?.document(userId)
                ?.collection(COLLECTION_NAME)
                ?.document(it)
                ?.set(workoutToCreate)
        }
    }

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

    // --- UPDATE ---

    fun updateWorkout(
        userId: String, workoutName: String?, workoutDate: Date?,
        exercisesList: ArrayList<Exercise>?
    ): Task<Void>? {
        val workoutToUpdate = Workout(workoutName, workoutDate, exercisesList)
        return workoutName?.let {
            UserHelper.getUsersCollection()
                ?.document(userId)
                ?.collection(COLLECTION_NAME)
                ?.document(it)
                ?.set(workoutToUpdate)
        }
    }

/*

    // --- DELETE ---

    fun deleteWorkout(workoutId: String): Task<Void?>? {
        return getWorkoutsCollection()?.document(workoutId)?.delete()
    }*/
}