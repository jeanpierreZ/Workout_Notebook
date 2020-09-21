package com.jpz.workoutnotebook.models

data class User(
    var userId: String = "",
    var nickName: String? = null,
    var name: String? = null,
    var firstName: String? = null,
    var age: Int? = null,
    var photo: Int? = null,
    var sports: String? = null,
    var iFollow: ArrayList<String>? = ArrayList(),
    var followers: ArrayList<String>? = ArrayList(),
    var exercisesList: ArrayList<String>? = ArrayList(),
    var workoutsList: ArrayList<String>? = ArrayList()
)