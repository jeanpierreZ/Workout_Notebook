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

    val userId: String = userViewModel.getUserUid()

    // --- CREATE ---

    fun createWorkout(workout: Workout) = workoutRepository.createWorkout(userId, workout)
        .addOnFailureListener { e -> Log.e(TAG, "Error writing document", e) }

    // --- READ ---

    fun getWorkout(workoutId: String) = workoutRepository.getWorkout(userId, workoutId)
        .addOnFailureListener { e -> Log.d(TAG, "get failed with ", e) }

    // --- QUERY ---

    fun getOrderedListOfWorkouts() = workoutRepository.getOrderedListOfWorkouts(userId)

    fun getListOfWorkouts() = workoutRepository.getListOfWorkouts(userId)

    // --- UPDATE ---

    fun updateWorkoutIdAfterCreate(documentReference: DocumentReference) =
        workoutRepository.updateWorkoutIdAfterCreate(userId, documentReference)
            .addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }

    fun updateWorkout(previousWorkout: Workout, workout: Workout) =
        workoutRepository.updateWorkout(userId, previousWorkout, workout)
            ?.addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }
}