package com.jpz.workoutnotebook.models

data class Exercise(
    var exerciseName: String? = null,
    var restNextSet: Int? = null,
    var restNextExercise: Int? = null,
    var editable: Boolean = true,
    var seriesList: ArrayList<Series>? = ArrayList()
)