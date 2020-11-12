package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.repositories.ExerciseRepository

class ExerciseViewModel(private val exerciseRepository: ExerciseRepository) : ViewModel() {

    companion object {
        private val TAG = ExerciseViewModel::class.java.simpleName
    }

    // --- CREATE ---

    fun createExercise(userId: String, exercise: Exercise) =
        exerciseRepository.createExercise(userId, exercise)
            ?.addOnFailureListener { e -> Log.e(TAG, "Error writing document", e) }

    // --- READ ---

    fun getExercise(userId: String, exerciseId: String) =
        exerciseRepository.getExercise(userId, exerciseId)
            ?.addOnFailureListener { e -> Log.e(TAG, "get failed with ", e) }

    // --- QUERY ---

    fun getOrderedListOfExercises(userId: String) =
        exerciseRepository.getOrderedListOfExercises(userId)

    fun getListOfExercises(userId: String) = exerciseRepository.getListOfExercises(userId)

    // --- UPDATE ---

    fun updateExercise(userId: String, previousExercise: Exercise, exercise: Exercise) =
        exerciseRepository.updateExercise(userId, previousExercise, exercise)
            ?.addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }

    fun updateExerciseIdAfterCreate(userId: String, documentReference: DocumentReference) =
        exerciseRepository.updateExerciseIdAfterCreate(userId, documentReference)
            ?.addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }
}