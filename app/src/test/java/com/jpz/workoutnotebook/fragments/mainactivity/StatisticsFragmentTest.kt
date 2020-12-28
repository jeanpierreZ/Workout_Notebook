package com.jpz.workoutnotebook.fragments.mainactivity

import android.os.Bundle
import com.jpz.workoutnotebook.utils.DatePickerFragment
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito

class StatisticsFragmentTest {

    private val statisticsFragment = StatisticsFragment()
    private val bundle: Bundle = Mockito.mock(Bundle::class.java)

    private val year: Int = 2021
    private val month: Int = 0
    private val day: Int = 4
    private val expected = "4 janv. 2021"

    private fun mockBundle() {
        Mockito.`when`(bundle.getInt(DatePickerFragment.BUNDLE_KEY_YEAR)).thenReturn(year)
        Mockito.`when`(bundle.getInt(DatePickerFragment.BUNDLE_KEY_MONTH)).thenReturn(month)
        Mockito.`when`(bundle.getInt(DatePickerFragment.BUNDLE_KEY_DAY)).thenReturn(day)
    }

    @Test
    fun setCalendarEntryDateTest() {
        mockBundle()
        val actual: String = statisticsFragment.setCalendarEntryDate(bundle)
        assertEquals(expected, actual)
    }

    @Test
    fun setCalendarEndDateTest() {
        mockBundle()
        val actual: String = statisticsFragment.setCalendarEndDate(bundle)
        assertEquals(expected, actual)
    }
}