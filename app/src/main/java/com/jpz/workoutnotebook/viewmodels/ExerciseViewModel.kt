package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModel
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.models.Exercise
import com.jpz.workoutnotebook.repositories.ExerciseRepository
import com.jpz.workoutnotebook.utils.MyUtils
import org.koin.java.KoinJavaComponent.inject

class ExerciseViewModel(private val exerciseRepository: ExerciseRepository) : ViewModel() {

    companion object {
        private val TAG = ExerciseViewModel::class.java.simpleName
    }

    private val myUtils: MyUtils by inject(MyUtils::class.java)

    // --- CREATE ---

    fun createExercise(coordinatorLayout: CoordinatorLayout, userId: String, exercise: Exercise) =
        exerciseRepository.createExercise(userId, exercise)
            ?.addOnSuccessListener { documentReference ->
                // Set exerciseId
                exerciseRepository.updateExerciseIdAfterCreate(userId, documentReference)
                // Inform the user
                myUtils.showSnackBar(
                    coordinatorLayout, coordinatorLayout.context.getString(
                        R.string.new_exercise_created, exercise.exerciseName
                    )
                )
                Log.d(TAG, "DocumentSnapshot written with name: ${documentReference.id}")
            }?.addOnFailureListener { e ->
                Log.e(TAG, "Error writing document", e)
            }

    // --- READ ---

    fun getExercise(userId: String, exerciseId: String) =
        exerciseRepository.getExercise(userId, exerciseId)?.addOnFailureListener { e ->
            Log.e(TAG, "get failed with ", e)
        }

    // --- QUERY ---

    fun getOrderedListOfExercises(userId: String) =
        exerciseRepository.getOrderedListOfExercises(userId)

    fun getListOfExercises(userId: String) = exerciseRepository.getListOfExercises(userId)

    // --- UPDATE ---

    fun updateExercise(
        coordinatorLayout: CoordinatorLayout,
        userId: String, previousExercise: Exercise, exercise: Exercise
    ) =
        exerciseRepository.updateExercise(userId, previousExercise, exercise)
            ?.addOnSuccessListener {
                myUtils.showSnackBar(
                    coordinatorLayout, coordinatorLayout.context.getString(
                        R.string.exercise_updated, exercise.exerciseName
                    )
                )
                Log.d(TAG, "DocumentSnapshot successfully updated!")
            }?.addOnFailureListener { e ->
                Log.e(TAG, "Error updating document", e)
            }
}