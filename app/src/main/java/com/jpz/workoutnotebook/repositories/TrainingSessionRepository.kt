package com.jpz.workoutnotebook.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.jpz.workoutnotebook.models.TrainingSession

class TrainingSessionRepository {

    companion object {
        const val COLLECTION_NAME = "trainingSessions"
        private const val TRAINING_SESSION_ID_FIELD = "trainingSessionId"
    }

    // --- CREATE ---

    fun createTrainingSession(
        userId: String, trainingSession: TrainingSession
    ): Task<DocumentReference>? =
        UserRepository.getUsersCollection()
            ?.document(userId)
            ?.collection(COLLECTION_NAME)
            ?.add(trainingSession)

    // --- QUERY ---

    fun getListOfTrainingSessions(userId: String): Query? =
        UserRepository.getUsersCollection()?.document(userId)?.collection(COLLECTION_NAME)

    // --- UPDATE ---

    fun updateTrainingSessionIdAfterCreate(
        userId: String, documentReference: DocumentReference
    ): Task<Void>? =
        UserRepository.getUsersCollection()
            ?.document(userId)
            ?.collection(COLLECTION_NAME)
            ?.document(documentReference.id)
            // Use SetOptions.merge() to only update the trainingSessionId
            ?.set(hashMapOf(TRAINING_SESSION_ID_FIELD to documentReference.id), SetOptions.merge())

    fun updateTrainingSession(userId: String, trainingSession: TrainingSession): Task<Void>? =
        trainingSession.trainingSessionId?.let {
            UserRepository.getUsersCollection()
                ?.document(userId)
                ?.collection(COLLECTION_NAME)
                ?.document(it)
                ?.set(trainingSession)
        }

    // --- DELETE ---

    fun deleteATrainingSession(userId: String, trainingSession: TrainingSession): Task<Void>? {
        return trainingSession.trainingSessionId?.let {
            UserRepository.getUsersCollection()
                ?.document(userId)
                ?.collection(COLLECTION_NAME)
                ?.document(it)
                ?.delete()
        }
    }
}