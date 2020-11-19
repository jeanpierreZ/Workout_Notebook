package com.jpz.workoutnotebook.models

data class Followers(
    var userId: String = "",
    var nickName: String? = null,
    var name: String? = null,
    var firstName: String? = null,
    var age: Int? = null,
    var photoProfile: String? = null,
    var sports: String? = null
)