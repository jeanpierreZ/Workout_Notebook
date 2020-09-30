package com.jpz.workoutnotebook.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.Series
import java.util.*

class ExerciseHelper {

    companion object {
        private const val COLLECTION_NAME = "exercises"
    }

    // --- CREATE ---

    fun createExercise(
        userId: String, exerciseName: String?, restNextSet: Int?,
        restNextExercise: Int?, editable: Boolean, seriesList: ArrayList<Series>?
    ): Task<DocumentReference>? {
        val exerciseToCreate =
            Exercise(exerciseName, restNextSet, restNextExercise, editable, seriesList)
        return UserHelper.getUsersCollection()?.document(userId)?.collection(COLLECTION_NAME)
            ?.add(exerciseToCreate)
    }

    // --- READ ---

    /*fun getExercise(exerciseId: String): Task<DocumentSnapshot>? =
        getExercisesCollection()?.document(exerciseId)?.get()*/

    // --- QUERY ---

    fun getListOfExercises(userId: String): Query? =
        UserHelper.getUsersCollection()
            ?.document(userId)
            ?.collection(COLLECTION_NAME)

    // --- UPDATE ---

    /*fun updateExercise(exercise: Exercise): Task<Void>? {
        return getExercisesCollection()?.document(exercise.exerciseId)?.set(exercise)
    }*/

    // --- DELETE ---

    /*fun deleteExercise(exerciseId: String): Task<Void?>? {
        return getExercisesCollection()?.document(exerciseId)?.delete()
    }*/
}