package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModel
import com.jpz.workoutnotebook.R
import com.jpz.workoutnotebook.repositories.TrainingSessionRepository
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.utils.MyUtils
import org.koin.java.KoinJavaComponent

class TrainingSessionViewModel(private val trainingSessionRepository: TrainingSessionRepository) :
    ViewModel() {

    companion object {
        private val TAG = TrainingSessionViewModel::class.java.simpleName
    }

    private val myUtils: MyUtils by KoinJavaComponent.inject(MyUtils::class.java)

    // --- CREATE ---

    fun createTrainingSession(
        coordinatorLayout: CoordinatorLayout, userId: String, trainingSession: TrainingSession
    ) = trainingSessionRepository.createTrainingSession(userId, trainingSession)
        ?.addOnSuccessListener { documentReference ->
            // Set trainingSessionId
            trainingSessionRepository.updateTrainingSessionIdAfterCreate(userId, documentReference)
            // Inform the user
            myUtils.showSnackBar(
                coordinatorLayout, coordinatorLayout.context.getString(
                    R.string.new_training_session_created, trainingSession.workout?.workoutName
                )
            )
            Log.d(TAG, "DocumentSnapshot written with name: ${documentReference.id}")
        }?.addOnFailureListener { e ->
            Log.e(TAG, "Error writing document", e)
        }

    // --- QUERY ---

    fun getListOfTrainingSessions(userId: String) =
        trainingSessionRepository.getListOfTrainingSessions(userId)

    // --- UPDATE ---

    fun updateTrainingSession(
        coordinatorLayout: CoordinatorLayout, userId: String, trainingSession: TrainingSession
    ) = trainingSessionRepository.updateTrainingSession(userId, trainingSession)
        ?.addOnSuccessListener {
            myUtils.showSnackBar(coordinatorLayout, R.string.training_session_updated)
            Log.d(TAG, "DocumentSnapshot successfully updated!")
        }?.addOnFailureListener { e ->
            Log.e(TAG, "Error updating document", e)
        }

    // --- DELETE ---

    fun deleteATrainingSession(
        coordinatorLayout: CoordinatorLayout, userId: String, trainingSession: TrainingSession
    ) = trainingSessionRepository.deleteATrainingSession(userId, trainingSession)
        ?.addOnSuccessListener {
            myUtils.showSnackBar(coordinatorLayout, R.string.training_session_deleted)
            Log.d(TAG, "DocumentSnapshot successfully deleted!")
        }?.addOnFailureListener { e ->
            Log.e(TAG, "Error deleted document", e)
        }
}