package com.jpz.workoutnotebook.utils

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {

    private const val RESOURCE = "FIRESTORE_CALL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun incrementIdlingResource() =
        countingIdlingResource.increment()

    fun decrementIdlingResource() {
        if (!countingIdlingResource.isIdleNow) countingIdlingResource.decrement()
    }
}