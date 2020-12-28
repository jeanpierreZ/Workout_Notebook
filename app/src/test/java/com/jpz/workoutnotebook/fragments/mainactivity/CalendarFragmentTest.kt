package com.jpz.workoutnotebook.fragments.mainactivity

import org.junit.Assert.assertEquals
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragmentTest {

    private val calendarFragment = CalendarFragment()
    private val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

    private val yearOfTraining: Int = 2021
    private val monthOfTraining: Int = 0
    private val dayOfTraining: Int = 4
    private val expectedSdfDateOfTraining = "2021.01.04 00:00"
    private val expectedSdfDayAfterTraining = "2021.01.05 00:00"

    @Test
    fun getDateOfTrainingTest() {
        val actualSdfDateOfTraining: String =
            calendarFragment.getDateOfTraining(yearOfTraining, monthOfTraining, dayOfTraining, sdf)
        assertEquals(expectedSdfDateOfTraining, actualSdfDateOfTraining)
    }

    @Test
    fun getDayAfterTrainingTest() {
        val actualSdfDayAfterTraining: String = calendarFragment
            .getDayAfterTraining(yearOfTraining, monthOfTraining, dayOfTraining, sdf)
        assertEquals(expectedSdfDayAfterTraining, actualSdfDayAfterTraining)
    }
}