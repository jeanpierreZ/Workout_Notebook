package com.jpz.workoutnotebook.models

data class Exercise(
    var exerciseId: String = "",
    var exerciseName: String? = null,
    var restNextSet: Int? = null,
    var restNextExercise: Int? = null,
    var editable: Boolean = true,
    var setsList: ArrayList<String>? = ArrayList()
)