package com.jpz.workoutnotebook.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentReference
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.repositories.TrainingSessionRepository

class TrainingSessionViewModel(private val trainingSessionRepository: TrainingSessionRepository) :
    ViewModel() {

    companion object {
        private val TAG = TrainingSessionViewModel::class.java.simpleName
    }

    // --- CREATE ---

    fun createTrainingSession(userId: String, trainingSession: TrainingSession) =
        trainingSessionRepository.createTrainingSession(userId, trainingSession)
            ?.addOnFailureListener { e -> Log.e(TAG, "Error writing document", e) }

    // --- QUERY ---

    fun getListOfTrainingSessions(userId: String) =
        trainingSessionRepository.getListOfTrainingSessions(userId)

    // --- UPDATE ---

    fun updateTrainingSessionIdAfterCreate(userId: String, documentReference: DocumentReference) =
        trainingSessionRepository.updateTrainingSessionIdAfterCreate(userId, documentReference)
            ?.addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }

    fun updateTrainingSession(userId: String, trainingSession: TrainingSession) =
        trainingSessionRepository.updateTrainingSession(userId, trainingSession)
            ?.addOnFailureListener { e -> Log.e(TAG, "Error updating document", e) }

    // --- DELETE ---

    fun deleteATrainingSession(userId: String, trainingSession: TrainingSession) =
        trainingSessionRepository.deleteATrainingSession(userId, trainingSession)
            ?.addOnFailureListener { e -> Log.e(TAG, "Error deleted document", e) }
}