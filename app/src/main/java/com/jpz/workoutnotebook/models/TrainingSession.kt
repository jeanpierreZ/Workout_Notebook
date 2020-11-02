package com.jpz.workoutnotebook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrainingSession(
    var trainingSessionId: String? = null,
    var trainingSessionDate: String? = null,
    var trainingSessionCompleted: Boolean = false,
    var workout: Workout? = null
) : Parcelable