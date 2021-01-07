package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.repositories.TrainingSessionRepository

class TrainingSessionViewModel(
    private val trainingSessionRepository: TrainingSessionRepository, userViewModel: UserViewModel
) :
    ViewModel() {

    companion object {
        private val TAG = TrainingSessionViewModel::class.java.simpleName
    }

    val userId: String = userViewModel.getUserUid()

    // --- CREATE ---

    fun createTrainingSession(trainingSession: TrainingSession) =
        trainingSessionRepository.createTrainingSession(userId, trainingSession)
            .addOnFailureListener { e -> Log.e(TAG, "Error writing document", e) }

    // --- QUERY ---

    // Get the list of training session for the current user
    fun getListOfTrainingSessions() = trainingSessionRepository.getListOfTrainingSessions(userId)

    // Get the list of training session for the followed and followers people
    fun getListOfTrainingSessionsOfFollow(FollowId: String) =
        trainingSessionRepository.getListOfTrainingSessions(FollowId)

    // --- UPDATE ---

    fun updateTrainingSessionIdAfterCreate(documentReference: DocumentReference) =
        trainingSessionRepository.updateTrainingSessionIdAfterCreate(userId, documentReference)
            .addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }

    fun updateTrainingSession(trainingSession: TrainingSession) =
        trainingSessionRepository.updateTrainingSession(userId, trainingSession)
            ?.addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }

    // --- DELETE ---

    fun deleteATrainingSession(trainingSession: TrainingSession) =
        trainingSessionRepository.deleteATrainingSession(userId, trainingSession)
            ?.addOnFailureListener { e -> Log.e(TAG, "Error deleted document", e) }
}