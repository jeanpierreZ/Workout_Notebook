package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.models.Workout
import com.jpz.workoutnotebook.repositories.WorkoutRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository, userViewModel: UserViewModel,
    private val trainingSessionViewModel: TrainingSessionViewModel

) : ViewModel() {

    companion object {
        private val TAG = WorkoutViewModel::class.java.simpleName
        private const val WORKOUT_FIELD = "workout"
        private const val TRAINING_SESSION_COMPLETED_FIELD = "trainingSessionCompleted"
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
        // First update the workout
        workoutRepository.updateWorkout(it, workout)
            ?.addOnSuccessListener {
                // Then update this workout in all training sessions that contain it
                // Find training sessions that are not completed and that contain this workout
                trainingSessionViewModel.getListOfTrainingSessions()
                    ?.whereEqualTo(TRAINING_SESSION_COMPLETED_FIELD, false)
                    ?.whereEqualTo(WORKOUT_FIELD, previousWorkout)
                    ?.get()
                    ?.addOnSuccessListener { documents ->
                        Log.d(TAG, "documents = ${documents.documents}")
                        if (documents.isEmpty) {
                            Log.w(TAG, "documents.isEmpty")
                        } else {
                            GlobalScope.launch {
                                for (document in documents) {
                                    // For each training session
                                    // Get TrainingSession object
                                    val trainingSession =
                                        document.toObject(TrainingSession::class.java)
                                    // Update this workout in the trainingSession
                                    trainingSession.workout = workout
                                    // Launch a job and wait it has finished
                                    val job: Job = launch {
                                        // Update the trainingSession
                                        trainingSessionViewModel
                                            .updateTrainingSession(trainingSession)
                                            ?.addOnCompleteListener {
                                                Log.d(TAG, "DocumentSnapshot successfully updated!")
                                            }
                                    }
                                    // Wait that updateTrainingSession has finished before setting the next document in loop
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
}