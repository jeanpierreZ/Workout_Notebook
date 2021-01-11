package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.Workout
import com.jpz.workoutnotebook.repositories.ExerciseRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ExerciseViewModel(
    private val exerciseRepository: ExerciseRepository, userViewModel: UserViewModel,
    private val workoutViewModel: WorkoutViewModel
) : ViewModel() {

    companion object {
        private val TAG = ExerciseViewModel::class.java.simpleName
        private const val EXERCISE_LIST_FIELD = "exercisesList"
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
        // First update the exercise
        exerciseRepository.updateExercise(it, exercise)
            ?.addOnSuccessListener {
                // Then update this exercise in all workouts that contain it
                // Find all workouts that contain this exercise
                workoutViewModel.getListOfWorkouts()
                    ?.whereArrayContains(EXERCISE_LIST_FIELD, previousExercise)
                    ?.get()
                    ?.addOnSuccessListener { documents ->
                        Log.d(TAG, "documents = ${documents.documents}")
                        if (documents.isEmpty) {
                            Log.w(TAG, "documents.isEmpty")
                        } else {
                            GlobalScope.launch {
                                for (document in documents) {
                                    // For each workout
                                    // Get Workout object to update
                                    val workout = document.toObject(Workout::class.java)
                                    // Get a workout before changes, which is used to update the training sessions
                                    val previousWorkout = document.toObject(Workout::class.java)
                                    // Get the position of this exercise in the list
                                    val index: Int = workout.exercisesList.indexOf(previousExercise)
                                    // Update this exercise in the list
                                    workout.exercisesList[index] = exercise
                                    // Launch a job and wait it has finished
                                    val job: Job = launch {
                                        // Update this exercise in the workout
                                        workoutViewModel.updateWorkout(previousWorkout, workout)
                                            ?.addOnCompleteListener {
                                                Log.d(TAG, "DocumentSnapshot successfully updated!")
                                            }
                                    }
                                    // Wait that updateWorkout has finished before setting the next document in loop
                                    job.join()
                                }
                            }
                        }
                    }
                    ?.addOnFailureListener { exception ->
                        Log.e(TAG, "Error getting documents: ", exception)
                    }
            }
            ?.addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }
    }

    fun updateExerciseIdAfterCreate(documentReference: DocumentReference) = userId?.let {
        exerciseRepository.updateExerciseIdAfterCreate(it, documentReference)
            .addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }
    }
}