package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModel
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.api.WorkoutHelper
import com.jpz.workoutnotebook.models.Workout
import com.jpz.workoutnotebook.utils.MyUtils
import org.koin.java.KoinJavaComponent

class WorkoutViewModel(private val workoutHelper: WorkoutHelper) : ViewModel() {

    companion object {
        private val TAG = WorkoutViewModel::class.java.simpleName
    }

    private val myUtils: MyUtils by KoinJavaComponent.inject(MyUtils::class.java)

    // --- CREATE ---

    fun createWorkout(coordinatorLayout: CoordinatorLayout, userId: String, workout: Workout) =
        workoutHelper.createWorkout(userId, workout)
            ?.addOnSuccessListener { documentReference ->
                // Set workoutId
                workoutHelper.updateWorkoutIdAfterCreate(userId, documentReference)
                // Inform the user
                myUtils.showSnackBar(
                    coordinatorLayout, coordinatorLayout.context.getString(
                        R.string.new_workout_created, workout.workoutName
                    )
                )
                Log.d(TAG, "DocumentSnapshot written with name: ${documentReference.id}")
            }?.addOnFailureListener { e ->
                Log.e(TAG, "Error writing document", e)
            }

    // --- READ ---

    // TODO use id
    fun getWorkout(userId: String, workoutName: String) =
        workoutHelper.getWorkout(userId, workoutName)?.addOnFailureListener { e ->
            Log.d(TAG, "get failed with ", e)
        }

    // --- QUERY ---

    fun getOrderedListOfExercises(userId: String) = workoutHelper.getOrderedListOfExercises(userId)

    fun getListOfExercises(userId: String) = workoutHelper.getListOfExercises(userId)

    // --- UPDATE ---

    fun updateWorkout(
        coordinatorLayout: CoordinatorLayout, userId: String, workout: Workout
    ) = workoutHelper.updateWorkout(userId, workout)
        ?.addOnSuccessListener { _ ->
            myUtils.showSnackBar(
                coordinatorLayout, coordinatorLayout.context.getString(
                    R.string.workout_updated, workout.workoutName
                )
            )
            Log.d(TAG, "DocumentSnapshot successfully updated!")
        }?.addOnFailureListener { e ->
            Log.e(TAG, "Error updating document", e)
        }
}