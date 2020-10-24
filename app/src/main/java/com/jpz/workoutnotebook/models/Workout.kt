package com.jpz.workoutnotebook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Workout(
    var workoutId: String? = null,
    var workoutName: String? = null,
    var exercisesList: ArrayList<Exercise>? = ArrayList()
) : Parcelable