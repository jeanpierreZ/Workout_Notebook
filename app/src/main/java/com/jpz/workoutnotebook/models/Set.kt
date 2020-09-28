package com.jpz.workoutnotebook.models

data class Set(
    var setId: String = "",
    var setName: String? = null,
    var reps: Int? = null,
    var numberOfUnit: Double? = null,
    var unit: String? = null
)