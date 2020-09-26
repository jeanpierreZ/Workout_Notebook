package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jpz.workoutnotebook.api.ExerciseHelper
import com.jpz.workoutnotebook.models.Exercise
import java.util.*

class ExerciseViewModel(private val exerciseHelper: ExerciseHelper) : ViewModel() {

    // --- CREATE ---

    fun createExercise(
        userId: String, exerciseId: String, exerciseName: String?, restNextSet: Int?,
        restNextExercise: Int?, editable: Boolean, setsList: ArrayList<String>?
    ) = exerciseHelper.createExercise(
        userId, exerciseId, exerciseName, restNextSet,
        restNextExercise, editable, setsList
    )
        ?.addOnFailureListener { e ->
            Log.e("createExercise", "Error writing document", e)
        }

    // --- READ ---

    /*fun getExercise(exerciseId: String) =
        exerciseHelper.getExercise(exerciseId)?.addOnFailureListener { e ->
            Log.d("getExercise", "get failed with ", e)
        }*/

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