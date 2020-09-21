package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.jpz.workoutnotebook.api.ExerciseHelper
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.models.Set
import java.util.*

class ExerciseViewModel(private val exerciseHelper: ExerciseHelper) : ViewModel() {

    // --- CREATE ---

    fun createExercise(
        exerciseId: String,
        exerciseName: String?,
        restNextSet: Int?,
        restNextExercise: Int?,
        editable: Boolean,
        sets: ArrayList<Set>?
    ) = exerciseHelper.createExercise(
        exerciseId,
        exerciseName,
        restNextSet,
        restNextExercise,
        editable,
        sets
    )
        ?.addOnFailureListener { e ->
            Log.e("createExercise", "Error writing document", e)
        }

    // --- READ ---

    fun getExercise(exerciseId: String) =
        exerciseHelper.getExercise(exerciseId)?.addOnFailureListener { e ->
            Log.d("getExercise", "get failed with ", e)
        }

    // --- UPDATE ---

    fun updateExercise(exerciseId: Exercise) =
        exerciseHelper.updateExercise(exerciseId)?.addOnFailureListener { e ->
            Log.e("updateExercise", "Error updating document", e)
        }


    // --- DELETE ---

    fun deleteExercise(exerciseId: String) =
        exerciseHelper.deleteExercise(exerciseId)?.addOnFailureListener { e ->
            Log.e("deleteExercise", "Error deleting document", e)
        }
}