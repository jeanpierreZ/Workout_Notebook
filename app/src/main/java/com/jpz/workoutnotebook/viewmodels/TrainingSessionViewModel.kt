package com.jpz.workoutnotebook.viewmodels

import android.content.Context
import android.util.Log
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.api.TrainingSessionHelper
import com.jpz.workoutnotebook.models.TrainingSession
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

    // --- UPDATE ---

    fun updateTrainingSession(
        coordinatorLayout: CoordinatorLayout,
        userId: String, trainingSessionDate: String?, workout: Workout?
    ) = trainingSessionHelper.updateTrainingSession(userId, trainingSessionDate, workout)
        ?.addOnSuccessListener { _ ->
            myUtils.showSnackBar(
                coordinatorLayout, R.string.training_session_updated
            )
            Log.d(TAG, "DocumentSnapshot successfully updated!")
        }?.addOnFailureListener { e ->
            Log.e(TAG, "Error updating document", e)
        }

    // --- DELETE ---

    fun deleteATrainingSession(
        coordinatorLayout: CoordinatorLayout, userId: String,
        trainingSession: TrainingSession, context: Context
    ) = trainingSessionHelper.deleteATrainingSession(userId, trainingSession)
        ?.addOnSuccessListener {
            showUndoSnackbar(coordinatorLayout, context, userId, trainingSession)
            Log.d(TAG, "DocumentSnapshot successfully deleted!")
        }?.addOnFailureListener { e ->
            Log.e(TAG, "Error deleted document", e)
        }

    private fun showUndoSnackbar(
        coordinatorLayout: CoordinatorLayout, context: Context, userId: String,
        trainingSession: TrainingSession
    ) {
        val snackbar: Snackbar = Snackbar.make(
            coordinatorLayout, context.getString(R.string.training_session_deleted),
            Snackbar.LENGTH_LONG
        )
        // Set action to undo delete the training session
        snackbar.setAction(context.getString(R.string.undo)) {
            undoDelete(coordinatorLayout, userId, trainingSession)
        }
        snackbar.show()
    }

    private fun undoDelete(
        coordinatorLayout: CoordinatorLayout, userId: String, trainingSession: TrainingSession
    ) = createTrainingSession(
        coordinatorLayout, userId, trainingSession.trainingSessionDate, trainingSession.workout
    )
}