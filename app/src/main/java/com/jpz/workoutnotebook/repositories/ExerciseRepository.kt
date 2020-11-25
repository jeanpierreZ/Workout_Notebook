package com.jpz.workoutnotebook.repositories

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.Workout

class ExerciseRepository {

    companion object {
        private val TAG = ExerciseRepository::class.java.simpleName
        private const val COLLECTION_NAME = "exercises"
        private const val EXERCISE_ID_FIELD = "exerciseId"
        private const val EXERCISE_NAME_FIELD = "exerciseName"
        private const val EXERCISE_LIST_FIELD = "exercisesList"
    }

    // --- CREATE ---

    fun createExercise(userId: String, exercise: Exercise): Task<DocumentReference> =
        UserRepository.getUsersCollection()
            .document(userId)
            .collection(COLLECTION_NAME)
            .add(exercise)

    // --- READ ---

    fun getExercise(userId: String, exerciseId: String): Task<DocumentSnapshot> =
        UserRepository.getUsersCollection()
            .document(userId)
            .collection(COLLECTION_NAME)
            .document(exerciseId)
            .get()

    // --- QUERY ---

    fun getListOfExercises(userId: String): Query =
        UserRepository.getUsersCollection().document(userId).collection(COLLECTION_NAME)

    fun getOrderedListOfExercises(userId: String): Query =
        UserRepository.getUsersCollection().document(userId).collection(COLLECTION_NAME)
            .orderBy(EXERCISE_NAME_FIELD, Query.Direction.ASCENDING)

    // --- UPDATE ---

    fun updateExerciseIdAfterCreate(userId: String, documentReference: DocumentReference) =
        UserRepository.getUsersCollection()
            .document(userId)
            .collection(COLLECTION_NAME)
            .document(documentReference.id)
            // Use SetOptions.merge() to only update the exerciseId
            .set(hashMapOf(EXERCISE_ID_FIELD to documentReference.id), SetOptions.merge())

    fun updateExercise(
        userId: String, previousExercise: Exercise, exercise: Exercise
    ): Task<QuerySnapshot>? =
        // First update the exercise
        exercise.exerciseId?.let {
            UserRepository.getUsersCollection()
                .document(userId)
                .collection(COLLECTION_NAME)
                .document(it)
                .set(exercise)

            // Then update this exercise in all workouts that contain it
            Log.d(TAG, "previousExercise = $previousExercise")
            // Find all workouts that contain this exercise
            val workoutRepository = WorkoutRepository()
            workoutRepository.getListOfWorkouts(userId)
                .whereArrayContains(EXERCISE_LIST_FIELD, previousExercise)
                .get()
                .addOnSuccessListener { documents ->
                    Log.d(TAG, "documents = ${documents.documents}")
                    if (documents.isEmpty) {
                        Log.w(TAG, "documents.isEmpty")
                    } else {
                        for (document in documents) {
                            // For each workout
                            workoutRepository.getWorkout(userId, document.id)
                                .addOnSuccessListener { doc ->
                                    // Get Workout object to update
                                    val workout = doc.toObject(Workout::class.java)
                                    // Get a workout before changes, which is used to update the training sessions
                                    val previousWorkout = doc.toObject(Workout::class.java)
                                    // Get the position of this exercise in the list
                                    val index: Int? =
                                        workout?.exercisesList?.indexOf(previousExercise)
                                    // Update this exercise in the list
                                    index?.let { workout.exercisesList.set(index, exercise) }
                                    // Update this exercise in the workout
                                    workout?.let {
                                        if (previousWorkout != null) {
                                            workoutRepository.updateWorkout(
                                                userId, previousWorkout, workout
                                            )
                                                ?.addOnSuccessListener {
                                                    Log.d(
                                                        TAG,
                                                        "DocumentSnapshot successfully updated!"
                                                    )
                                                }
                                                ?.addOnFailureListener { e ->
                                                    Log.e(TAG, "Error updating document", e)
                                                }
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