package com.jpz.workoutnotebook.repositories

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.models.Workout

class WorkoutRepository {

    companion object {
        private val TAG = WorkoutRepository::class.java.simpleName
        private const val COLLECTION_NAME = "workouts"
        private const val WORKOUT_FIELD = "workout"
        private const val WORKOUT_ID_FIELD = "workoutId"
        private const val WORKOUT_NAME_FIELD = "workoutName"
        private const val TRAINING_SESSION_COMPLETED_FIELD = "trainingSessionCompleted"
    }

    // --- CREATE ---

    fun createWorkout(userId: String, workout: Workout): Task<DocumentReference> =
        UserRepository.getUsersCollection()
            .document(userId)
            .collection(COLLECTION_NAME)
            .add(workout)

    // --- READ ---

    fun getWorkout(userId: String, workoutId: String): Task<DocumentSnapshot> =
        UserRepository.getUsersCollection()
            .document(userId)
            .collection(COLLECTION_NAME)
            .document(workoutId)
            .get()

    // --- QUERY ---

    fun getListOfWorkouts(userId: String): Query =
        UserRepository.getUsersCollection().document(userId).collection(COLLECTION_NAME)

    fun getOrderedListOfWorkouts(userId: String): Query =
        UserRepository.getUsersCollection().document(userId).collection(COLLECTION_NAME)
            .orderBy(WORKOUT_NAME_FIELD, Query.Direction.ASCENDING)

    // --- UPDATE ---

    fun updateWorkoutIdAfterCreate(userId: String, documentReference: DocumentReference) =
        UserRepository.getUsersCollection()
            .document(userId)
            .collection(COLLECTION_NAME)
            .document(documentReference.id)
            // Use SetOptions.merge() to only update the workoutId
            .set(hashMapOf(WORKOUT_ID_FIELD to documentReference.id), SetOptions.merge())

    fun updateWorkout(
        userId: String, previousWorkout: Workout, workout: Workout
    ): Task<QuerySnapshot>? =
        // First update the workout
        workout.workoutId?.let {
            UserRepository.getUsersCollection()
                .document(userId)
                .collection(COLLECTION_NAME)
                .document(it)
                .set(workout)

            // Then update this workout in all training sessions that contain it
            Log.d(TAG, "previousWorkout = $previousWorkout")
            // Find training sessions that are not completed and that contain this workout
            val trainingSessionRepository = TrainingSessionRepository()
            trainingSessionRepository.getListOfTrainingSessions(userId)
                .whereEqualTo(TRAINING_SESSION_COMPLETED_FIELD, false)
                .whereEqualTo(WORKOUT_FIELD, previousWorkout)
                .get()
                .addOnSuccessListener { documents ->
                    Log.d(TAG, "documents = ${documents.documents}")
                    if (documents.isEmpty) {
                        Log.w(TAG, "documents.isEmpty")
                    } else {
                        for (document in documents) {
                            // For each training session
                            trainingSessionRepository.getTrainingSession(userId, document.id)
                                .addOnSuccessListener { doc ->
                                    // Get TrainingSession object
                                    val trainingSession = doc.toObject(TrainingSession::class.java)
                                    // Update this workout in the trainingSession
                                    trainingSession?.workout = workout
                                    // Update the trainingSession
                                    trainingSession?.let {
                                        trainingSessionRepository.updateTrainingSession(
                                            userId, trainingSession
                                        )
                                            ?.addOnSuccessListener {
                                                Log.d(TAG, "DocumentSnapshot successfully updated!")
                                            }
                                            ?.addOnFailureListener { e ->
                                                Log.e(TAG, "Error updating document", e)
                                            }
                                    }
                                }
                                .addOnFailureListener { e -> Log.d(TAG, "get failed with ", e) }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
}