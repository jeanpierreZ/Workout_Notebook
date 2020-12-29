package com.jpz.workoutnotebook.fragments.mainactivity

import android.os.Bundle
import com.jpz.workoutnotebook.utils.DatePickerFragment
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class StatisticsFragmentTest {

    private val statisticsFragment = StatisticsFragment()
    private val mockedBundle: Bundle = Mockito.mock(Bundle::class.java)

    private val year: Int = 2021
    private val month: Int = 0
    private val day: Int = 4
    private val expected = "4 janv. 2021"

    private fun mockBundle() {
        Mockito.`when`(mockedBundle.getInt(DatePickerFragment.BUNDLE_KEY_YEAR)).thenReturn(year)
        Mockito.`when`(mockedBundle.getInt(DatePickerFragment.BUNDLE_KEY_MONTH)).thenReturn(month)
        Mockito.`when`(mockedBundle.getInt(DatePickerFragment.BUNDLE_KEY_DAY)).thenReturn(day)
    }

    @Before
    fun setUp() {
        mockBundle()
    }

    @Test
    fun setCalendarEntryDateTest() {
        val actual: String = statisticsFragment.setCalendarEntryDate(mockedBundle)
        assertEquals(expected, actual)
    }

    @Test
    fun setCalendarEndDateTest() {
        val actual: String = statisticsFragment.setCalendarEndDate(mockedBundle)
        assertEquals(expected, actual)
    }
}