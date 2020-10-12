package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModel
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.api.WorkoutHelper
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.utils.MyUtils
import org.koin.java.KoinJavaComponent
import java.util.*

class WorkoutViewModel(private val workoutHelper: WorkoutHelper) : ViewModel() {

    companion object {
        private val TAG = WorkoutViewModel::class.java.simpleName
    }

    private val myUtils: MyUtils by KoinJavaComponent.inject(MyUtils::class.java)

    // --- CREATE ---

    fun createWorkout(
        coordinatorLayout: CoordinatorLayout, userId: String, workoutName: String?,
        workoutDate: Date?, exercisesList: ArrayList<Exercise>?
    ) = workoutHelper.createWorkout(userId, workoutName, workoutDate, exercisesList)
        ?.addOnSuccessListener { _ ->
            myUtils.showSnackBar(
                coordinatorLayout,
                coordinatorLayout.context.getString(R.string.new_workout_created, workoutName)
            )
            Log.d(TAG, "DocumentSnapshot written with name: $workoutName")
        }?.addOnFailureListener { e ->
            Log.e(TAG, "Error writing document", e)
        }

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