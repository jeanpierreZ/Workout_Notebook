package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModel
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.api.TrainingSessionHelper
import com.jpz.workoutnotebook.models.Workout
import com.jpz.workoutnotebook.utils.MyUtils
import org.koin.java.KoinJavaComponent

class TrainingSessionViewModel(private val trainingSessionHelper: TrainingSessionHelper) :
    ViewModel() {

    companion object {
        private val TAG = TrainingSessionViewModel::class.java.simpleName
    }

    private val myUtils: MyUtils by KoinJavaComponent.inject(MyUtils::class.java)

    // --- CREATE ---

    fun createTrainingSession(
        coordinatorLayout: CoordinatorLayout, userId: String,
        trainingSessionDate: String?, workout: Workout?
    ) = trainingSessionHelper.createTrainingSession(
        userId, trainingSessionDate, workout
    )?.addOnSuccessListener { _ ->
        myUtils.showSnackBar(
            coordinatorLayout, coordinatorLayout.context.getString(
                R.string.new_training_session_created,
                workout?.workoutName
            )
        )
        Log.d(TAG, "DocumentSnapshot written with name: $trainingSessionDate")
    }?.addOnFailureListener { e ->
        Log.e(TAG, "Error writing document", e)
    }

    // --- QUERY ---

    fun getListOfTrainingSessions(userId: String) =
        trainingSessionHelper.getListOfTrainingSessions(userId)
}