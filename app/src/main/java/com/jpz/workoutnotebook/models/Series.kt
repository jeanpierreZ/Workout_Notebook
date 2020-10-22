package com.jpz.workoutnotebook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Series(
    var seriesName: String? = null,
    var reps: Int? = null,
    var numberOfUnit: Double? = null,
    var unit: String? = null
) : Parcelable