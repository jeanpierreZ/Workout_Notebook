package com.jpz.workoutnotebook.fragments.editactivity

import android.os.Bundle
import com.jpz.workoutnotebook.utils.DatePickerFragment
import com.jpz.workoutnotebook.utils.TimePickerFragment
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.*


class EditCalendarFragmentTest {

    private val editCalendarFragment = EditCalendarFragment()
    private val mockedBundle: Bundle = Mockito.mock(Bundle::class.java)

    private val year: Int = 2021
    private val month: Int = 0
    private val day: Int = 4
    private val hour = 16
    private val minute = 30
    private val expectedCalendarDate = "4 janv. 2021"
    private val expectedCalendarTime = "16:30"
    private val expectedTrainingSessionDate = "2021.01.04 16:30"

    private fun mockBundle() {
        Mockito.`when`(mockedBundle.getInt(DatePickerFragment.BUNDLE_KEY_YEAR)).thenReturn(year)
        Mockito.`when`(mockedBundle.getInt(DatePickerFragment.BUNDLE_KEY_MONTH)).thenReturn(month)
        Mockito.`when`(mockedBundle.getInt(DatePickerFragment.BUNDLE_KEY_DAY)).thenReturn(day)
        Mockito.`when`(mockedBundle.getInt(TimePickerFragment.BUNDLE_KEY_HOUR)).thenReturn(hour)
        Mockito.`when`(mockedBundle.getInt(TimePickerFragment.BUNDLE_KEY_MINUTE)).thenReturn(minute)
    }

    @Before
    fun setUp() {
        mockBundle()
    }

    @Test
    fun setCalendarDateTest() {
        val actualCalendarDate: String = editCalendarFragment.setCalendarDate(mockedBundle)
        assertEquals(expectedCalendarDate, actualCalendarDate)
    }

    @Test
    fun setCalendarTimeTest() {
        val actualCalendarTime: String = editCalendarFragment.setCalendarTime(mockedBundle)
        assertEquals(expectedCalendarTime, actualCalendarTime)
    }

    @Test
    fun checkIfDateToRegisterBeforeNowTest() {
        val yearToRegister = 2200
        val calendar = Calendar.getInstance()
        calendar.set(yearToRegister, month, day, hour, minute)
        val date: Date = calendar.time
        val actualDateToRegister = editCalendarFragment.checkIfDateToRegisterBeforeNow(date)
        // Assert false because the date to register and compare to now is in 2200
        Assert.assertFalse(actualDateToRegister)
    }

    @Test
    fun getTrainingSessionDateInSDFFormatTest() {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        val date: Date = calendar.time
        val actualTrainingSessionDate: String =
            editCalendarFragment.getTrainingSessionDateInSDFFormat(date)
        assertEquals(expectedTrainingSessionDate, actualTrainingSessionDate)
    }
}