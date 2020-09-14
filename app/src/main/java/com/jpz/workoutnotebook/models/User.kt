package com.jpz.workoutnotebook.models

data class User(
    var userId: String,
    var nickName: String? = null,
    var name: String? = null,
    var firstName: String? = null,
    var age: Int? = null,
    var photo: Boolean = false,
    var sports: String? = null,
    var iFollow: ArrayList<String>? = ArrayList(),
    var followers: ArrayList<String>? = ArrayList()
) {
    constructor() : this("", null, null, null, 0, false, null, null, null)
}
