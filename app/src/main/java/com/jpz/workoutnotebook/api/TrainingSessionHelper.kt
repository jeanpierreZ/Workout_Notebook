package com.jpz.workoutnotebook.api

import com.google.android.gms.tasks.Task
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.models.Workout

class TrainingSessionHelper {

    companion object {
        private const val COLLECTION_NAME = "trainingSessions"
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

}