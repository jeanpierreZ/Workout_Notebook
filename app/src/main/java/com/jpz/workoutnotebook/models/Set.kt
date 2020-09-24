package com.jpz.workoutnotebook.models

data class Set(
    var setId: String = "",
    var setName: String? = null,
    var reps: Int? = null,
    var unit: String? = null,
    var numberOfUnit: Int? = null
)