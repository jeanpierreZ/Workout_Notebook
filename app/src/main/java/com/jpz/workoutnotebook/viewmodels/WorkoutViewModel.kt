package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.jpz.workoutnotebook.models.Workout
import com.jpz.workoutnotebook.repositories.WorkoutRepository

class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository, userViewModel: UserViewModel
) : ViewModel() {

    companion object {
        private val TAG = WorkoutViewModel::class.java.simpleName
    }

    val userId: String? = userViewModel.getUserUid()

    // --- CREATE ---

    fun createWorkout(workout: Workout) = userId?.let {
        workoutRepository.createWorkout(it, workout)
            .addOnFailureListener { e -> Log.e(TAG, "Error writing document", e) }
    }

    // --- READ ---

    fun getWorkout(workoutId: String) = userId?.let {
        workoutRepository.getWorkout(it, workoutId)
            .addOnFailureListener { e -> Log.d(TAG, "get failed with ", e) }
    }

    // --- QUERY ---

    fun getOrderedListOfWorkouts() = userId?.let { workoutRepository.getOrderedListOfWorkouts(it) }

    fun getListOfWorkouts() = userId?.let { workoutRepository.getListOfWorkouts(it) }

    // --- UPDATE ---

    fun updateWorkoutIdAfterCreate(documentReference: DocumentReference) = userId?.let {
        workoutRepository.updateWorkoutIdAfterCreate(it, documentReference)
            .addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }
    }

    fun updateWorkout(previousWorkout: Workout, workout: Workout) = userId?.let {
        workoutRepository.updateWorkout(it, previousWorkout, workout)
            ?.addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }
    }
}