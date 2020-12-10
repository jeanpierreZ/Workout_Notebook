package com.jpz.workoutnotebook.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class User(
    var userId: String = "",
    var nickName: String? = null,
    var name: String? = null,
    var firstName: String? = null,
    var age: Int? = null,
    var photoProfile: String? = null,
    var sports: String? = null
) : Parcelable