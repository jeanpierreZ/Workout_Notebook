package com.jpz.workoutnotebook.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.Series
import java.util.*

class ExerciseHelper {

    companion object {
        private const val COLLECTION_NAME = "exercises"
        private const val EXERCISE_NAME_FIELD = "exerciseName"
    }

    // --- CREATE ---

    fun createExercise(
        userId: String, exerciseName: String?, restNextSet: Int?,
        restNextExercise: Int?, editable: Boolean, seriesList: ArrayList<Series>?
    ): Task<Void>? {
        val exerciseToCreate =
            Exercise(exerciseName, restNextSet, restNextExercise, editable, seriesList)
        return exerciseName?.let {
            UserHelper.getUsersCollection()
                ?.document(userId)
                ?.collection(COLLECTION_NAME)
                ?.document(it)
                ?.set(exerciseToCreate)
        }
    }

    // --- READ ---

    fun getExercise(userId: String, exerciseName: String): Task<DocumentSnapshot>? =
        UserHelper.getUsersCollection()
            ?.document(userId)
            ?.collection(COLLECTION_NAME)
            ?.document(exerciseName)
            ?.get()

    // --- QUERY ---

    fun getListOfExercises(userId: String): Query? =
        UserHelper.getUsersCollection()?.document(userId)?.collection(COLLECTION_NAME)

    fun getOrderedListOfExercises(userId: String): Query? =
        UserHelper.getUsersCollection()?.document(userId)?.collection(COLLECTION_NAME)
            ?.orderBy(EXERCISE_NAME_FIELD, Query.Direction.ASCENDING)

    // --- UPDATE ---

    fun updateExercise(
        userId: String, exerciseName: String?, restNextSet: Int?,
        restNextExercise: Int?, editable: Boolean, seriesList: ArrayList<Series>?
    ): Task<Void>? {
        val exerciseToUpdate =
            Exercise(exerciseName, restNextSet, restNextExercise, editable, seriesList)
        return exerciseName?.let {
            UserHelper.getUsersCollection()
                ?.document(userId)
                ?.collection(COLLECTION_NAME)
                ?.document(it)
                ?.set(exerciseToUpdate)
        }
    }
}