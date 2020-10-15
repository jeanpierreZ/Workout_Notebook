package com.jpz.workoutnotebook.models

import java.util.*
import kotlin.collections.ArrayList

data class Workout(
    var workoutName: String? = null,
    var workoutDate: Date? = null,
    var exercisesList: ArrayList<Exercise>? = ArrayList()
)