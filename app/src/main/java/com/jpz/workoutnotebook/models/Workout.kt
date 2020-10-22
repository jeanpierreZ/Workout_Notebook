package com.jpz.workoutnotebook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Workout(
    var workoutName: String? = null,
    // TODO delete workoutDate
    var workoutDate: Date? = null,
    var exercisesList: ArrayList<Exercise>? = ArrayList()
) : Parcelable