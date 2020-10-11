package com.jpz.workoutnotebook.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*
import kotlin.collections.ArrayList

data class Workout(
    var workoutName: String? = null,
    @ServerTimestamp
    var workoutDate: Date? = null,
    var exercisesList: ArrayList<String>? = ArrayList()
)