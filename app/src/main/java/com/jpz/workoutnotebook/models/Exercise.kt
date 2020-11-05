package com.jpz.workoutnotebook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Exercise(
    var exerciseId: String? = null,
    var exerciseName: String? = null,
    var restNextSet: Int = 0,
    var restNextExercise: Int = 0,
    var editable: Boolean = true,
    var seriesList: ArrayList<Series> = ArrayList()
) : Parcelable