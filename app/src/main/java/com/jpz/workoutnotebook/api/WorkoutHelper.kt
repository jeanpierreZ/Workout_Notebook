package com.jpz.workoutnotebook.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.jpz.workoutnotebook.models.Workout
import java.util.*

class WorkoutHelper {

    companion object {
        private const val COLLECTION_NAME = "workouts"
        fun getWorkoutsCollection(): CollectionReference? =
            FirebaseFirestore.getInstance().collection(COLLECTION_NAME)
    }

    // --- CREATE ---

    fun createWorkout(
        workoutId: String, workoutName: String, workoutDate: Date, exercisesList: ArrayList<String>?
    ): Task<Void>? {
        val workoutToCreate = Workout(workoutId, workoutName, workoutDate, exercisesList)
        return getWorkoutsCollection()?.document(workoutId)?.set(workoutToCreate)
    }

    // --- READ ---

    fun getWorkout(workoutId: String): Task<DocumentSnapshot>? =
        getWorkoutsCollection()?.document(workoutId)?.get()

    // --- UPDATE ---

    fun updateWorkout(workout: Workout): Task<Void>? {
        return getWorkoutsCollection()?.document(workout.workoutId)?.set(workout)
    }

    // --- DELETE ---

    fun deleteWorkout(workoutId: String): Task<Void?>? {
        return getWorkoutsCollection()?.document(workoutId)?.delete()
    }
}