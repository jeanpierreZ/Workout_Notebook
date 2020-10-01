package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModel
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.api.ExerciseHelper
import com.jpz.workoutnotebook.models.Series
import com.jpz.workoutnotebook.utils.MyUtils
import org.koin.java.KoinJavaComponent.inject
import java.util.*

class ExerciseViewModel(private val exerciseHelper: ExerciseHelper) : ViewModel() {

    companion object {
        private val TAG = ExerciseViewModel::class.java.simpleName
    }

    private val myUtils: MyUtils by inject(MyUtils::class.java)

    // --- CREATE ---

    fun createExercise(
        coordinatorLayout: CoordinatorLayout, userId: String, exerciseName: String?,
        restNextSet: Int?, restNextExercise: Int?, editable: Boolean, seriesList: ArrayList<Series>?
    ) = exerciseHelper.createExercise(
        userId, exerciseName, restNextSet,
        restNextExercise, editable, seriesList
    )
        ?.addOnSuccessListener { _ ->
            myUtils.showSnackBar(
                coordinatorLayout,
                coordinatorLayout.context.getString(R.string.new_exercise_created, exerciseName)
            )
            Log.d(TAG, "DocumentSnapshot written with name: $exerciseName")
        }
        ?.addOnFailureListener { e ->
            Log.e(TAG, "Error writing document", e)
        }

    // --- READ ---

    fun getExercise(userId: String, exerciseName: String) =
        exerciseHelper.getExercise(userId, exerciseName)?.addOnFailureListener { e ->
            Log.d(TAG, "get failed with ", e)
        }

    // --- QUERY ---

    fun getListOfExercises(userId: String) = exerciseHelper.getListOfExercises(userId)

    // --- UPDATE ---

    /* fun updateExercise(exerciseId: Exercise) =
         exerciseHelper.updateExercise(exerciseId)?.addOnFailureListener { e ->
             Log.e("updateExercise", "Error updating document", e)
         }*/


    // --- DELETE ---

    /*fun deleteExercise(exerciseId: String) =
        exerciseHelper.deleteExercise(exerciseId)?.addOnFailureListener { e ->
            Log.e("deleteExercise", "Error deleting document", e)
        }*/
}