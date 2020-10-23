package com.jpz.workoutnotebook.api

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.Query
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.models.Workout

class TrainingSessionHelper {

    companion object {
        const val COLLECTION_NAME = "trainingSessions"
    }

    // --- CREATE ---

    fun createTrainingSession(
        userId: String, trainingSessionDate: String?, workout: Workout?
    ): Task<Void>? {
        val trainingSessionToCreate =
            TrainingSession(trainingSessionDate, workout)
        return trainingSessionDate?.let {
            UserHelper.getUsersCollection()
                ?.document(userId)
                ?.collection(COLLECTION_NAME)
                ?.document(it)
                ?.set(trainingSessionToCreate)
        }
    }

    // --- QUERY ---

    fun getListOfTrainingSessions(userId: String): Query? =
        UserHelper.getUsersCollection()?.document(userId)?.collection(COLLECTION_NAME)

    // --- UPDATE ---

    fun updateTrainingSession(
        userId: String, trainingSessionDate: String?, workout: Workout?
    ): Task<Void>? {
        val trainingSessionToUpdate = TrainingSession(trainingSessionDate, workout)
        return trainingSessionDate?.let {
            UserHelper.getUsersCollection()
                ?.document(userId)
                ?.collection(COLLECTION_NAME)
                ?.document(it)
                ?.set(trainingSessionToUpdate)
        }
    }

    // --- DELETE ---

    fun deleteATrainingSession( userId: String, trainingSession: TrainingSession): Task<Void>? {
        return trainingSession.trainingSessionDate?.let {
            UserHelper.getUsersCollection()
                ?.document(userId)
                ?.collection(COLLECTION_NAME)
                ?.document(it)
                ?.delete()
        }
    }
}