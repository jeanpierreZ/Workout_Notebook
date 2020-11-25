package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.jpz.workoutnotebook.models.Workout
import com.jpz.workoutnotebook.repositories.WorkoutRepository

class WorkoutViewModel(private val workoutRepository: WorkoutRepository) : ViewModel() {

    companion object {
        private val TAG = WorkoutViewModel::class.java.simpleName
    }

    // --- CREATE ---

    fun createWorkout(userId: String, workout: Workout) =
        workoutRepository.createWorkout(userId, workout)
            .addOnFailureListener { e -> Log.e(TAG, "Error writing document", e) }

    // --- READ ---

    fun getWorkout(userId: String, workoutId: String) =
        workoutRepository.getWorkout(userId, workoutId)
            .addOnFailureListener { e -> Log.d(TAG, "get failed with ", e) }

    // --- QUERY ---

    fun getOrderedListOfWorkouts(userId: String) =
        workoutRepository.getOrderedListOfWorkouts(userId)

    fun getListOfWorkouts(userId: String) = workoutRepository.getListOfWorkouts(userId)

    // --- UPDATE ---

    fun updateWorkoutIdAfterCreate(userId: String, documentReference: DocumentReference) =
        workoutRepository.updateWorkoutIdAfterCreate(userId, documentReference)
            .addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }

    fun updateWorkout(userId: String, previousWorkout: Workout, workout: Workout) =
        workoutRepository.updateWorkout(userId, previousWorkout, workout)
            ?.addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }
}