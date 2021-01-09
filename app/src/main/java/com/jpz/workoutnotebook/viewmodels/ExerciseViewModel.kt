package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.repositories.ExerciseRepository

class ExerciseViewModel(
    private val exerciseRepository: ExerciseRepository, userViewModel: UserViewModel
) : ViewModel() {

    companion object {
        private val TAG = ExerciseViewModel::class.java.simpleName
    }

    val userId: String? = userViewModel.getUserUid()

    // --- CREATE ---

    fun createExercise(exercise: Exercise) = userId?.let {
        exerciseRepository.createExercise(it, exercise)
            .addOnFailureListener { e -> Log.e(TAG, "Error writing document", e) }
    }

    // --- READ ---

    fun getExercise(exerciseId: String) = userId?.let {
        exerciseRepository.getExercise(it, exerciseId)
            .addOnFailureListener { e -> Log.e(TAG, "get failed with ", e) }
    }

    // --- QUERY ---

    fun getOrderedListOfExercises() =
        userId?.let { exerciseRepository.getOrderedListOfExercises(it) }

    fun getListOfExercises() = userId?.let { exerciseRepository.getListOfExercises(it) }

    // --- UPDATE ---

    fun updateExercise(previousExercise: Exercise, exercise: Exercise) = userId?.let {
        exerciseRepository.updateExercise(it, previousExercise, exercise)
            ?.addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }
    }

    fun updateExerciseIdAfterCreate(documentReference: DocumentReference) = userId?.let {
        exerciseRepository.updateExerciseIdAfterCreate(it, documentReference)
            .addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }
    }
}