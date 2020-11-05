package com.jpz.workoutnotebook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Series(
    var seriesName: String? = null,
    var reps: Int = 0,
    var numberOfUnit: Double = 0.0,
    var unit: String = Unit.BLANK.stringValue
) : Parcelable