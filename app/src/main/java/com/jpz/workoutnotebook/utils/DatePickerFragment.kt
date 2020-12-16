package com.jpz.workoutnotebook.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import java.util.*

class DatePickerFragment(
    private val historical: Boolean, private val entryDate: Boolean,
    private var year: Int?, private var month: Int?, private var day: Int?
) : DialogFragment(), DatePickerDialog.OnDateSetListener {

    companion object {
        const val REQUEST_KEY_ENTRY_DATE = "REQUEST_KEY_ENTRY_DATE"
        const val REQUEST_KEY_DATE = "REQUEST_KEY_DATE"
        const val BUNDLE_KEY_YEAR = "BUNDLE_KEY_YEAR"
        const val BUNDLE_KEY_MONTH = "BUNDLE_KEY_MONTH"
        const val BUNDLE_KEY_DAY = "BUNDLE_KEY_DAY"
        private const val MILLISECONDS_IN_A_SECOND = 1000
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (year == null && month == null && day == null) {
            // Use the current date as the default date in the picker
            val c = Calendar.getInstance()
            year = c.get(Calendar.YEAR)
            month = c.get(Calendar.MONTH)
            day = c.get(Calendar.DAY_OF_MONTH)
        }

        // Set DatePickerDialog with date from the calendar
        val dialog = activity?.let { DatePickerDialog(it, this, year!!, month!!, day!!) }

        if (historical) {
            // Configure the calendar to not choose a future date
            dialog?.datePicker?.maxDate = System.currentTimeMillis() + MILLISECONDS_IN_A_SECOND
        } else {
            // Configure the calendar to not choose a past date
            dialog?.datePicker?.minDate = System.currentTimeMillis() - MILLISECONDS_IN_A_SECOND
        }
        return dialog!!
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        if (entryDate) {
            // Send the result to StatisticsFragment for the entryDate
            setFragmentResult(
                REQUEST_KEY_ENTRY_DATE,
                bundleOf(BUNDLE_KEY_YEAR to year, BUNDLE_KEY_MONTH to month, BUNDLE_KEY_DAY to day)
            )
        } else {
            // Send the result to EditCalendarFragment or to StatisticsFragment for the endDate
            setFragmentResult(
                REQUEST_KEY_DATE,
                bundleOf(BUNDLE_KEY_YEAR to year, BUNDLE_KEY_MONTH to month, BUNDLE_KEY_DAY to day)
            )
        }
    }
}