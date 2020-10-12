package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jpz.workoutnotebook.api.WorkoutHelper
import com.jpz.workoutnotebook.models.Workout
import org.koin.java.KoinJavaComponent
import java.util.*

class WorkoutViewModel(private val workoutHelper: WorkoutHelper) : ViewModel() {

    companion object {
        private val TAG = WorkoutViewModel::class.java.simpleName
    }

/*
    // --- CREATE ---

    fun createWorkout(
        workoutId: String, workoutName: String, workoutDate: Date, exercisesList: ArrayList<String>?
    ) = workoutHelper.createWorkout(workoutId, workoutName, workoutDate, exercisesList)
        ?.addOnFailureListener { e ->
            Log.e("createWorkout", "Error writing document", e)
        }
*/

    // --- READ ---

    fun getWorkout(userId: String, exerciseName: String) =
        workoutHelper.getWorkout(userId, exerciseName)?.addOnFailureListener { e ->
            Log.d(TAG, "get failed with ", e)
        }

    // --- QUERY ---

    fun getOrderedListOfExercises(userId: String) = workoutHelper.getOrderedListOfExercises(userId)

    fun getListOfExercises(userId: String) = workoutHelper.getListOfExercises(userId)


/*
    // --- UPDATE ---

    fun updateWorkout(workout: Workout) =
        workoutHelper.updateWorkout(workout)?.addOnFailureListener { e ->
            Log.e("updateWorkout", "Error updating document", e)
        }


    // --- DELETE ---

    fun deleteWorkout(workoutId: String) =
        workoutHelper.deleteWorkout(workoutId)?.addOnFailureListener { e ->
            Log.e("deleteWorkout", "Error deleting document", e)
        }*/
}