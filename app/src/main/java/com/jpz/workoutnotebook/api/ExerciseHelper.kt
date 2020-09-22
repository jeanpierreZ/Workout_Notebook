package com.jpz.workoutnotebook.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.jpz.workoutnotebook.models.Exercise
import java.util.*

class ExerciseHelper {

    companion object {
        private const val COLLECTION_NAME = "exercises"
        /*fun getExercisesCollection(): CollectionReference? =
            FirebaseFirestore.getInstance().collection(COLLECTION_NAME)*/
    }

    // --- CREATE ---

    fun createExercise(
        userId: String, exerciseId: String, exerciseName: String?, restNextSet: Int?,
        restNextExercise: Int?, editable: Boolean, setsList: ArrayList<String>?
    ): Task<DocumentReference>? {
        val exerciseToCreate =
            Exercise(exerciseId, exerciseName, restNextSet, restNextExercise, editable, setsList)
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