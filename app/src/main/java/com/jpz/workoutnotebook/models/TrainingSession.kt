package com.jpz.workoutnotebook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrainingSession(
    var trainingSessionDate: String? = null,
    var workout: Workout? = null
) : Parcelable