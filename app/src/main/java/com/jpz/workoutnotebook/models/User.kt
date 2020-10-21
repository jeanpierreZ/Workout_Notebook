package com.jpz.workoutnotebook.models

data class User(
    var userId: String = "",
    var nickName: String? = null,
    var name: String? = null,
    var firstName: String? = null,
    var age: Int? = null,
    var photoProfile: String? = null,
    var sports: String? = null,
    var iFollow: ArrayList<String>? = ArrayList(),
    var followers: ArrayList<String>? = ArrayList(),
    // TODO delete exercises and workouts
    var exercises: ArrayList<Exercise>? = ArrayList(),
    var workouts: ArrayList<Workout>? = ArrayList()
)