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

    val userId: String = userViewModel.getUserUid()

    // --- CREATE ---

    fun createExercise(exercise: Exercise) = exerciseRepository.createExercise(userId, exercise)
        .addOnFailureListener { e -> Log.e(TAG, "Error writing document", e) }

    // --- READ ---

    fun getExercise(exerciseId: String) = exerciseRepository.getExercise(userId, exerciseId)
        .addOnFailureListener { e -> Log.e(TAG, "get failed with ", e) }

    // --- QUERY ---

    fun getOrderedListOfExercises() = exerciseRepository.getOrderedListOfExercises(userId)

    fun getListOfExercises() = exerciseRepository.getListOfExercises(userId)

    // --- UPDATE ---

    fun updateExercise(previousExercise: Exercise, exercise: Exercise) =
        exerciseRepository.updateExercise(userId, previousExercise, exercise)
            ?.addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }

    fun updateExerciseIdAfterCreate(documentReference: DocumentReference) =
        exerciseRepository.updateExerciseIdAfterCreate(userId, documentReference)
            .addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }
}