package com.jpz.workoutnotebook.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.jpz.workoutnotebook.models.Exercise
import java.util.*

class ExerciseHelper {

    companion object {
        private const val COLLECTION_NAME = "exercises"
        fun getExercisesCollection(): CollectionReference? =
            Firebase.firestore.collection(COLLECTION_NAME)
    }

    // --- CREATE ---

    fun createExercise(
        exerciseId: String, exerciseName: String?, restNextSet: Int?,
        restNextExercise: Int?, editable: Boolean, setsList: ArrayList<String>?
    ): Task<Void>? {
        val exerciseToCreate =
            Exercise(exerciseId, exerciseName, restNextSet, restNextExercise, editable, setsList)
        return getExercisesCollection()?.document(exerciseId)?.set(exerciseToCreate)
    }

    // --- READ ---

    fun getExercise(exerciseId: String): Task<DocumentSnapshot>? =
        getExercisesCollection()?.document(exerciseId)?.get()

    // --- UPDATE ---

    fun updateExercise(exercise: Exercise): Task<Void>? {
        return getExercisesCollection()?.document(exercise.exerciseId)?.set(exercise)
    }

    // --- DELETE ---

    fun deleteExercise(exerciseId: String): Task<Void?>? {
        return getExercisesCollection()?.document(exerciseId)?.delete()
    }
}