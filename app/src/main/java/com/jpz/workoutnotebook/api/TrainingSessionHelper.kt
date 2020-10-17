package com.jpz.workoutnotebook.api

import com.google.android.gms.tasks.Task
import com.jpz.workoutnotebook.models.TrainingSession
import com.jpz.workoutnotebook.models.Workout
import java.util.*

class TrainingSessionHelper {

    companion object {
        private const val COLLECTION_NAME = "trainings"
    }

    // --- CREATE ---

    fun createTrainingSession(
        userId: String, trainingSessionId: String?, trainingSessionDate: Date?, workout: Workout?
    ): Task<Void>? {
        val trainingSessionToCreate =
            TrainingSession(trainingSessionId, trainingSessionDate, workout)
        return trainingSessionDate?.let {
            UserHelper.getUsersCollection()
                ?.document(userId)
                ?.collection(COLLECTION_NAME)
                ?.document(it.toString())
                ?.set(trainingSessionToCreate)
        }
    }

}