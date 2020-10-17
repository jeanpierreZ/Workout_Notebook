package com.jpz.workoutnotebook.models

import java.util.*

data class TrainingSession(
    var trainingSessionId: String? = null,
    var trainingSessionDate: Date? = null,
    var workout: Workout? = null
)